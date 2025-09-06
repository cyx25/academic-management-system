package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.ProjectRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.StudentTeste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.Teste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto.CreateTesteDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto.StudentTesteDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto.TesteDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.StudentTesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.TesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.domain.Enrollment;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.services.FinalGradeCalculationService;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.repository.CurriculumUnitRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.files.File;
import pt.ulisboa.tecnico.rnl.dei.dms.files.FileService;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TesteService {

    private final FinalGradeCalculationService finalGradeCalculationService;

    private final TesteRepository testeRepository;
    private final StudentTesteRepository studentTesteRepository;
    private final CurriculumUnitRepository curriculumUnitRepository;
    private final ProjectRepository projectRepository;
    private final PersonRepository personRepository;
    private final FileService fileService;

    public TesteService(TesteRepository testeRepository,
                       StudentTesteRepository studentTesteRepository,
                       CurriculumUnitRepository curriculumUnitRepository,
                       PersonRepository personRepository,
                        ProjectRepository projectRepository,
                       FileService fileService, FinalGradeCalculationService finalGradeCalculationService) {
        this.testeRepository = testeRepository;
        this.studentTesteRepository = studentTesteRepository;
        this.curriculumUnitRepository = curriculumUnitRepository;
        this.projectRepository = projectRepository;
        this.personRepository = personRepository;
        this.fileService = fileService;
        this.finalGradeCalculationService = finalGradeCalculationService;
    }

    @Transactional(readOnly = true)
    public List<TesteDto> getTestsByCurriculumUnit(Long curriculumUnitId) {
        List<Teste> testes = testeRepository.findByCurriculumUnitId(curriculumUnitId);
        return testes.stream()
                .map(TesteDto::new)
                .collect(Collectors.toList());
    }

    public TesteDto createTeste(Long curriculumUnitId, CreateTesteDto createTesteDto) {
        CurriculumUnit curriculumUnit = curriculumUnitRepository.findById(curriculumUnitId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_CU));

        validateTotalWeight(curriculumUnitId, createTesteDto.weight().intValue(), null);

        Teste teste = new Teste(createTesteDto, curriculumUnit);
        Teste savedTeste = testeRepository.save(teste);


        // Create StudentTeste entries for all enrolled students
        createStudentTestesForEnrolledStudents(savedTeste, curriculumUnit);

        return new TesteDto(savedTeste);
    }


   
    private void validateTotalWeight(Long curriculumUnitId, Integer newWeight, Long excludeTesteId) {
      
        List<Teste> existingTestes = testeRepository.findByCurriculumUnitId(curriculumUnitId);
        int currentTestsWeight = 0;
        
        for (Teste teste : existingTestes) {
           
            if (excludeTesteId == null || !teste.getId().equals(excludeTesteId)) {
                currentTestsWeight += teste.getWeight();
            }
        }

   
        var existingProjects = projectRepository.findByCurriculumUnitId(curriculumUnitId);
        int currentProjectsWeight = 0;
        
        for (var project : existingProjects) {
            currentProjectsWeight += project.getWeight();
        }

      
        int totalWeight = currentTestsWeight + currentProjectsWeight + newWeight;

        // Validate total doesn't exceed 100
        if (totalWeight > 100) {
            throw new DEIException(ErrorMessage.WEIGHT_EXCEEDS_MAXIMUM);
        }

        System.out.println("[DEBUG] Weight validation passed: Tests=" + currentTestsWeight + 
                          ", Projects=" + currentProjectsWeight + 
                          ", New=" + newWeight + 
                          ", Total=" + totalWeight);
    }

    public TesteDto addStatementFile(Long testeId, MultipartFile file) throws IOException {
        Teste teste = testeRepository.findById(testeId)
                .orElseThrow(() -> new DEIException(ErrorMessage.TESTE_NOT_FOUND));

        if (teste.getStatementFile() != null) {
            fileService.deleteFile(teste.getStatementFile().getPath());
        }

        File statementFile = fileService.storeFile(file);
        teste.setStatementFile(statementFile);
        
        Teste savedTeste = testeRepository.save(teste);
        return new TesteDto(savedTeste);
    }

    public TesteDto addCorrectionFile(Long testeId, MultipartFile file) throws IOException {
        Teste teste = testeRepository.findById(testeId)
                .orElseThrow(() -> new DEIException(ErrorMessage.TESTE_NOT_FOUND));

        if (teste.getCorrectionFile() != null) {
            fileService.deleteFile(teste.getCorrectionFile().getPath());
        }

        File correctionFile = fileService.storeFile(file);
        teste.setCorrectionFile(correctionFile);
        
        Teste savedTeste = testeRepository.save(teste);
        return new TesteDto(savedTeste);
    }

    @Transactional
    public List<StudentTesteDto> getStudentTestesByTeste(Long testeId) {
        
        List<StudentTeste> studentTestes = studentTesteRepository.findByTesteId(testeId);
        return studentTestes.stream()
                .map(StudentTesteDto::new)
                .collect(Collectors.toList());
    }

    public void gradeStudentTeste(Long studentTesteId, Long teacherId, Float grade) {
        StudentTeste studentTeste = studentTesteRepository.findById(studentTesteId)
                .orElseThrow(() -> new DEIException(ErrorMessage.STUDENT_TESTE_NOT_FOUND));

        Person teacher = personRepository.findById(teacherId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON));

        studentTeste.setGrade(grade);
        studentTeste.setGradedTeacher(teacher);
        studentTesteRepository.save(studentTeste);

        finalGradeCalculationService.calculateAndUpdateFinalGrade(
            studentTeste.getStudent().getId(),
            studentTeste.getTeste().getCurriculumUnit().getId()
    );
    }

    @Transactional(readOnly = true)
    public StudentTesteDto getStudentTeste(Long testeId, Long studentId) {
        StudentTeste studentTeste = studentTesteRepository.findByTesteIdAndStudentId(testeId, studentId)
                .orElseThrow(() -> new DEIException(ErrorMessage.STUDENT_TESTE_NOT_FOUND));
        
        return new StudentTesteDto(studentTeste);
    }

    @Transactional(readOnly = true)
    public List<StudentTesteDto> getStudentTestsForStudent(Long unitId, Long studentId) {
        // Use simple approach: get all student tests and filter
        List<StudentTeste> allStudentTestes = studentTesteRepository.findByStudentId(studentId);
        
        // Filter by curriculum unit
        List<StudentTeste> filteredTestes = allStudentTestes.stream()
                .filter(st -> st.getTeste().getCurriculumUnit().getId().equals(unitId))
                .collect(Collectors.toList());
        
        return filteredTestes.stream()
                .map(StudentTesteDto::new)
                .collect(Collectors.toList());
    }

   private void createStudentTestesForEnrolledStudents(Teste teste, CurriculumUnit curriculumUnit) {
        List<Person> enrolledStudents = curriculumUnit.getEnrollments().stream()
                .map(Enrollment::getStudent)
                .collect(Collectors.toList());

        for (Person student : enrolledStudents) {
            StudentTeste studentTeste = new StudentTeste(teste, student);
            System.out.println("Creating StudentTeste for student: " + student.getName());
            studentTesteRepository.save(studentTeste);
        }
    }

  
}