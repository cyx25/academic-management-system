package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.StudentTeste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.StudentTesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.domain.Revision;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.dto.CreateRevisionDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.dto.RevisionDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.repository.RevisionRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.services.FinalGradeCalculationService;
import pt.ulisboa.tecnico.rnl.dei.dms.email.EmailService;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RevisionService {

    private final FinalGradeCalculationService finalGradeCalculationService;
    private final EmailService emailService;
    private final RevisionRepository revisionRepository;
    private final StudentTesteRepository studentTesteRepository;
    private final PersonRepository personRepository;

    public RevisionService(RevisionRepository revisionRepository,
                          EmailService emailService,
                          StudentTesteRepository studentTesteRepository,
                          PersonRepository personRepository, FinalGradeCalculationService finalGradeCalculationService) {
        this.revisionRepository = revisionRepository;
        this.emailService = emailService;
        this.studentTesteRepository = studentTesteRepository;
        this.personRepository = personRepository;
        this.finalGradeCalculationService = finalGradeCalculationService;
    }

    public RevisionDto requestRevision(Long studentTesteId, String justification) {
        StudentTeste studentTeste = studentTesteRepository.findById(studentTesteId)
                .orElseThrow(() -> new DEIException(ErrorMessage.STUDENT_TESTE_NOT_FOUND));

        // Validate business rules
        if (studentTeste.getGrade() == null) {
            throw new DEIException(ErrorMessage.TEST_NOT_GRADED);
        }

        CreateRevisionDto createRevisionDto = new CreateRevisionDto(studentTesteId, justification);
        Revision revision = new Revision(createRevisionDto, studentTeste);
        Revision savedRevision = revisionRepository.save(revision);

        emailService.notifyRevisionRequested(
            studentTeste.getStudent().getEmail(),
            studentTeste.getStudent().getName(),
            studentTeste.getTeste().getCurriculumUnit().getName(),
            studentTeste.getTeste().getTitle()
        );
        
        // Send notification to main teacher
        Person mainTeacher = studentTeste.getTeste().getCurriculumUnit().getMainTeacher();
        if (mainTeacher != null) {
            emailService.notifyTeachersRevisionRequested(
                mainTeacher.getEmail(),
                mainTeacher.getName(),
                studentTeste.getStudent().getName(),
                studentTeste.getTeste().getCurriculumUnit().getName(),
                studentTeste.getTeste().getTitle()
            );
        }

        return new RevisionDto(savedRevision);
    }

    @Transactional(readOnly = true)
    public List<RevisionDto> getPendingRevisions(Long unitId) {
        // Get all revisions and filter by unit using streams
        List<Revision> allRevisions = revisionRepository.findAll();
        
        List<Revision> pendingRevisions = allRevisions.stream()
                .filter(revision -> {
                    // Check if revision belongs to the specified curriculum unit
                    Long revisionUnitId = revision.getStudentTeste()
                            .getTeste()
                            .getCurriculumUnit()
                            .getId();
                    return unitId.equals(revisionUnitId);
                })
                .filter(revision -> {
                    // Check if revision is pending or TA graded (needs processing)
                    Revision.Status status = revision.getStatus();
                    return Revision.Status.PENDING.equals(status) || 
                           Revision.Status.TA_GRADED.equals(status);
                })
                .sorted((r1, r2) -> r1.getRequestDate().compareTo(r2.getRequestDate()))
                .collect(Collectors.toList());
        
        return pendingRevisions.stream()
                .map(RevisionDto::new)
                .collect(Collectors.toList());
    }

    public RevisionDto gradeRevision(Long revisionId, Long teachingAssistantId, Float newGrade) {
        Revision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new DEIException(ErrorMessage.REVISION_NOT_FOUND));

        Person teachingAssistant = personRepository.findById(teachingAssistantId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON));

        // Validate revision status
        if (!Revision.Status.PENDING.equals(revision.getStatus())) {
            throw new DEIException(ErrorMessage.REVISION_ALREADY_PROCESSED);
        }


        // Update revision with TA's new grade
        revision.setStatus(Revision.Status.TA_GRADED);
        revision.setNewGrade(newGrade);
        revision.setReviewedBy(teachingAssistant);
        revision.setReviewDate(LocalDateTime.now());

        Revision savedRevision = revisionRepository.save(revision);

        emailService.notifyRevisionStatusChanged(
            revision.getStudentTeste().getStudent().getEmail(),
            revision.getStudentTeste().getStudent().getName(),
            revision.getStudentTeste().getTeste().getCurriculumUnit().getName(),
            revision.getStudentTeste().getTeste().getTitle(),
            "REVISTO PELO ASSISTENTE"
        );
        return new RevisionDto(savedRevision);
    }

    public RevisionDto processRevision(Long revisionId, String action, Long mainTeacherId) {
        Revision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new DEIException(ErrorMessage.REVISION_NOT_FOUND));

        Person mainTeacher = personRepository.findById(mainTeacherId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON));

        // Validate revision is ready for main teacher approval
        if (!Revision.Status.TA_GRADED.equals(revision.getStatus())) {
            throw new DEIException(ErrorMessage.REVISION_NOT_READY_FOR_APPROVAL);
        }

        // Process the main teacher's decision
        if ("APPROVE".equals(action)) {
            revision.setStatus(Revision.Status.APPROVED);
            
            // Update the student's actual grade with the new grade
            StudentTeste studentTeste = revision.getStudentTeste();
            studentTeste.setGrade(revision.getNewGrade());
            studentTeste.setGradedTeacher(mainTeacher);
            studentTesteRepository.save(studentTeste);

            finalGradeCalculationService.calculateAndUpdateFinalGrade(
                studentTeste.getStudent().getId(),
                studentTeste.getTeste().getCurriculumUnit().getId()
        );
            
        } else if ("REJECT".equals(action)) {
            revision.setStatus(Revision.Status.REJECTED);
            // Original grade remains unchanged
        } else {
            throw new DEIException(ErrorMessage.INVALID_REVISION_ACTION);
        }

        // Update final decision metadata
        revision.setReviewedBy(mainTeacher);
        revision.setReviewDate(LocalDateTime.now());

        Revision savedRevision = revisionRepository.save(revision);

        emailService.notifyRevisionStatusChanged(
            revision.getStudentTeste().getStudent().getEmail(),
            revision.getStudentTeste().getStudent().getName(),
            revision.getStudentTeste().getTeste().getCurriculumUnit().getName(),
            revision.getStudentTeste().getTeste().getTitle(),
            action
        );

        return new RevisionDto(savedRevision);
    }

    @Transactional(readOnly = true)
    public RevisionDto getRevisionByStudentTeste(Long studentTesteId) {
        StudentTeste studentTeste = studentTesteRepository.findById(studentTesteId)
                .orElseThrow(() -> new DEIException(ErrorMessage.STUDENT_TESTE_NOT_FOUND));
        
        if (!studentTeste.hasRevision()) {
            throw new DEIException(ErrorMessage.REVISION_NOT_FOUND);
        }
        
        return new RevisionDto(studentTeste.getRevision());
    }

    @Transactional(readOnly = true)
    public List<RevisionDto> getAllRevisionsByUnit(Long unitId) {
        // Get all revisions for a unit - useful for teachers to see all revisions
        List<Revision> allRevisions = revisionRepository.findAll();
        
        List<Revision> unitRevisions = allRevisions.stream()
                .filter(revision -> {
                    Long revisionUnitId = revision.getStudentTeste()
                            .getTeste()
                            .getCurriculumUnit()
                            .getId();
                    return unitId.equals(revisionUnitId);
                })
                .sorted((r1, r2) -> r2.getRequestDate().compareTo(r1.getRequestDate())) // Latest first
                .collect(Collectors.toList());
        
        return unitRevisions.stream()
                .map(RevisionDto::new)
                .collect(Collectors.toList());
    }
}