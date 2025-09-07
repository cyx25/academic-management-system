package pt.ulisboa.tecnico.rnl.dei.dms.person.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Project;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.StudentGroup;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.ProjectRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.StudentGroupRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.StudentTeste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.Teste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.StudentTesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.TesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.domain.Enrollment;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.AssessmentDeliveryDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.ProgressDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.UnitFinalGradeDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.repository.EnrollmentRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@Service
@Transactional
public class StudentService {
    
    private final PersonRepository personRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TesteRepository testeRepository;
    private final StudentTesteRepository studentTesteRepository;
    private final ProjectRepository projectRepository;
    private final StudentGroupRepository studentGroupRepository;

    public StudentService(
            PersonRepository personRepository,
            EnrollmentRepository enrollmentRepository,
            TesteRepository testeRepository,
            StudentTesteRepository studentTesteRepository,
            ProjectRepository projectRepository,
            StudentGroupRepository studentGroupRepository) {
        this.personRepository = personRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.testeRepository = testeRepository;
        this.studentTesteRepository = studentTesteRepository;
        this.projectRepository = projectRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    /**
     * Get student profile data - simple list of curriculum units with final grades
     */
    @Transactional(readOnly = true)
    public List<UnitFinalGradeDto> getStudentProfile(Long studentId) {
        System.out.println("[DEBUG] Getting profile for student: " + studentId);
        

        
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        
        return enrollments.stream()
                .map(enrollment -> new UnitFinalGradeDto(
                    enrollment.getCurriculumUnit().getId(),
                    enrollment.getCurriculumUnit().getName(),
                    enrollment.getFinalGrade()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get student assessment deliveries for calendar view
     */
    @Transactional(readOnly = true)
    public List<AssessmentDeliveryDto> getStudentAssessmentDeliveries(Long studentId) {
        System.out.println("[DEBUG] Getting assessment deliveries for student: " + studentId);
        
    
        List<AssessmentDeliveryDto> deliveries = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        
        for (Enrollment enrollment : enrollments) {
            String unitName = enrollment.getCurriculumUnit().getName();
            Long unitId = enrollment.getCurriculumUnit().getId();
            
            // Get test deliveries
            List<Teste> tests = testeRepository.findByCurriculumUnitId(unitId);
            for (Teste test : tests) {
                deliveries.add(new AssessmentDeliveryDto(
                    test.getDate(), // Use test date as submission time
                    unitName,
                    "TEST"
                ));
            }
            
            // Get project deliveries
            List<Project> projects = projectRepository.findByCurriculumUnitId(unitId);
            for (Project project : projects) {
                deliveries.add(new AssessmentDeliveryDto(
                    project.getDueDate(), // Use due date as submission time
                    unitName,
                    "PROJECT"
                ));
            }
        }
        
        // Sort by submission time
        deliveries.sort((a, b) -> a.submissionTime().compareTo(b.submissionTime()));
        
        System.out.println("[DEBUG] Found " + deliveries.size() + " assessment deliveries");
        return deliveries;
    }

    /**
     * Get student overall progress statistics
     */
    @Transactional(readOnly = true)
    public ProgressDto getStudentProgress(Long studentId) {
        System.out.println("[DEBUG] Getting progress for student: " + studentId);
        
    
    
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        
        int totalProjects = 0;
        int completedProjects = 0;
        int totalTests = 0;
        int completedTests = 0;
        int totalCurriculumUnits = enrollments.size();
        int completedCurriculumUnits = 0;
        
        List<Integer> grades = new ArrayList<>();
        
        for (Enrollment enrollment : enrollments) {
            Long unitId = enrollment.getCurriculumUnit().getId();
            
            // Count completed curriculum units (those with final grade)
            if (enrollment.getFinalGrade() != null) {
                completedCurriculumUnits++;
                grades.add(enrollment.getFinalGrade());
            }
            
            // Count projects
            List<Project> projects = projectRepository.findByCurriculumUnitId(unitId);
            totalProjects += projects.size();
            
            for (Project project : projects) {
                Optional<StudentGroup> studentGroup = studentGroupRepository
                        .findByProject_IdAndStudents_Id(project.getId(), studentId);
                if (studentGroup.isPresent() && studentGroup.get().getGrade() != null) {
                    completedProjects++;
                }
            }
            
            // Count tests
            List<Teste> tests = testeRepository.findByCurriculumUnitId(unitId);
            totalTests += tests.size();
            
            for (Teste test : tests) {
                Optional<StudentTeste> studentTest = studentTesteRepository
                        .findByTesteIdAndStudentId(test.getId(), studentId);
                if (studentTest.isPresent() && studentTest.get().getGrade() != null) {
                    completedTests++;
                }
            }
        }
        
        // Calculate average grade
        Float averageGrade = grades.isEmpty() ? null : 
                (float) grades.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        
        ProgressDto progress = new ProgressDto(
            totalProjects,
            completedProjects,
            totalTests,
            completedTests,
            averageGrade,
            totalCurriculumUnits,
            completedCurriculumUnits
        );
        
        System.out.println("[DEBUG] Progress: " + progress);
        return progress;
    }
}