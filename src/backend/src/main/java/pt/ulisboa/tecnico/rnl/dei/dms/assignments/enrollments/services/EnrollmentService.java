package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.services;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.StudentGroup;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.StudentGroupRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.StudentTeste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.StudentTesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.domain.Enrollment;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.AssessmentDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.EnrollmentDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.FinalGradeBreakdownDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.repository.EnrollmentRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.repository.CurriculumUnitRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.email.EmailService;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@Service
@Transactional
public class EnrollmentService {

   
    private final EnrollmentRepository enrollmentRepository;
    private final PersonRepository personRepository;
    private final StudentTesteRepository studentTesteRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final CurriculumUnitRepository curriculumUnitRepository;
    private final EmailService emailService;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            PersonRepository personRepository,
            EmailService emailService,
            StudentTesteRepository studentTesteRepository,
            CurriculumUnitRepository curriculumUnitRepository,
            StudentGroupRepository studentGroupRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.personRepository = personRepository;
        this.emailService = emailService;
        this.studentTesteRepository = studentTesteRepository;
        this.curriculumUnitRepository = curriculumUnitRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    public EnrollmentDto createEnrollment(long curriculumUnitId, long studentId) {
        CurriculumUnit curriculumUnit = curriculumUnitRepository.findById(curriculumUnitId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_CU, Long.toString(curriculumUnitId)));

        Person student = personRepository.findById(studentId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(studentId)));

        enrollmentRepository.findByCurriculumUnitIdAndStudentId(curriculumUnitId, studentId)
                .ifPresent(e -> {
                    throw new DEIException(ErrorMessage.ENROLLMENT_ALREADY_EXISTS, Long.toString(studentId));
                });

        Enrollment enrollment = new Enrollment(student, curriculumUnit);
        enrollmentRepository.save(enrollment);

        emailService.notifyStudentEnrolled(
            student.getEmail(),
            student.getName(),
            curriculumUnit.getName()
        );

        return new EnrollmentDto(enrollment);
    }

    public void deleteEnrollment(long curriculumUnitId, long studentId) {
        Enrollment enrollment = enrollmentRepository.findByCurriculumUnitIdAndStudentId(curriculumUnitId, studentId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_ENROLLMENT, Long.toString(studentId)));

        enrollmentRepository.delete(enrollment);
    }


    @Transactional(readOnly = true)
    public List<EnrollmentDto> getEnrollments(long curriculumUnitId) {
        // Validate that the curriculum unit exists
        if (!curriculumUnitRepository.existsById(curriculumUnitId)) {
            throw new DEIException(ErrorMessage.NO_SUCH_COURSE, Long.toString(curriculumUnitId));
        }

        return enrollmentRepository.findByCurriculumUnitId(curriculumUnitId)
                .stream()
                .map(EnrollmentDto::new)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public FinalGradeBreakdownDto getFinalGradeBreakdown(Long studentId, Long curriculumUnitId) {
        // Validate enrollment exists
        Enrollment enrollment = enrollmentRepository.findByCurriculumUnitIdAndStudentId(curriculumUnitId, studentId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_ENROLLMENT));
        
        System.out.println("[DEBUG] Getting final grade breakdown for student " + studentId + " in unit " + curriculumUnitId);

        // Gather all assessments
        List<AssessmentDto> assessments = gatherAllAssessments(studentId, curriculumUnitId);

        // Calculate total weight
        int totalWeight = assessments.stream()
                .mapToInt(AssessmentDto::weight)
                .sum();

        // Check if all assessments are graded
        boolean allGraded = assessments.stream()
                .allMatch(assessment -> assessment.grade() != null);
        
        // Determine if grade calculation is complete
        boolean isComplete = totalWeight == 100 && allGraded;

        System.out.println("[DEBUG] Breakdown summary: " + assessments.size() + " assessments, " +
                        "total weight: " + totalWeight + ", all graded: " + allGraded + ", complete: " + isComplete);
        
        return new FinalGradeBreakdownDto(
                studentId,
                enrollment.getStudent().getName(),
                curriculumUnitId,
                enrollment.getCurriculumUnit().getName(),
                assessments,
                totalWeight,
                enrollment.getFinalGrade(),
                isComplete
        );
    }

    private List<AssessmentDto> gatherAllAssessments(Long studentId, Long curriculumUnitId) {
        List<AssessmentDto> assessments = new ArrayList<>();

        // Get all test assignments for the student
        List<StudentTeste> studentTests = studentTesteRepository.findByStudentId(studentId);
        for (StudentTeste studentTest : studentTests) {
            // Filter by curriculum unit
            if (studentTest.getTeste().getCurriculumUnit().getId().equals(curriculumUnitId)) {
                AssessmentDto testAssessment = new AssessmentDto(
                        "TEST",
                        studentTest.getTeste().getTitle(),
                        studentTest.getTeste().getWeight().intValue(),
                        studentTest.getGrade()
                );
                assessments.add(testAssessment);
                System.out.println("[DEBUG] Added test: " + testAssessment.name() + 
                                " (weight: " + testAssessment.weight() + ", grade: " + testAssessment.grade() + ")");
            }
        }
        
        // Get all project assignments for the student
        List<StudentGroup> studentGroups = studentGroupRepository.findByStudents_Id(studentId);
        for (StudentGroup group : studentGroups) {
            // Filter by curriculum unit
            if (group.getProject().getCurriculumUnit().getId().equals(curriculumUnitId)) {
                AssessmentDto projectAssessment = new AssessmentDto(
                        "PROJECT",
                        group.getProject().getTitle(),
                        group.getProject().getWeight().intValue(),
                        group.getGrade()
                );
                assessments.add(projectAssessment);
                System.out.println("[DEBUG] Added project: " + projectAssessment.name() + 
                                " (weight: " + projectAssessment.weight() + ", grade: " + projectAssessment.grade() + ")");
            }
        }

        return assessments;
    }

   

    
}