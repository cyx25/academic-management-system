package pt.ulisboa.tecnico.rnl.dei.dms.person.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Project;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.StudentGroup;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.ProjectRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.StudentGroupRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.StudentTeste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.Teste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.domain.Revision;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.repository.RevisionRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.StudentTesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.TesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.domain.Enrollment;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.repository.EnrollmentRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.repository.CurriculumUnitRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.teachings.dto.PendingGradingDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.teachings.dto.TeacherStatisticsDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.TeacherStudentDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@Service
@Transactional
public class TeacherService {
    
    private final PersonRepository personRepository;
    private final CurriculumUnitRepository curriculumUnitRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProjectRepository projectRepository;
    private final TesteRepository testeRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final StudentTesteRepository studentTesteRepository;
    private final RevisionRepository revisionRepository;

    public TeacherService(
            PersonRepository personRepository,
            CurriculumUnitRepository curriculumUnitRepository,
            EnrollmentRepository enrollmentRepository,
            ProjectRepository projectRepository,
            TesteRepository testeRepository,
            StudentGroupRepository studentGroupRepository,
            StudentTesteRepository studentTesteRepository,
            RevisionRepository revisionRepository) {
        this.personRepository = personRepository;
        this.curriculumUnitRepository = curriculumUnitRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.projectRepository = projectRepository;
        this.testeRepository = testeRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.studentTesteRepository = studentTesteRepository;
        this.revisionRepository = revisionRepository;
    }

    /**
     * Get all students that belong to the teacher's curriculum units
     * Returns students with their associated curriculum unit codes
     */
    @Transactional(readOnly = true)
    public List<TeacherStudentDto> getTeacherStudents(Long teacherId) {
        System.out.println("[DEBUG] Getting students for teacher: " + teacherId);
        
       
     
        // Get teacher's curriculum units based on role
        List<CurriculumUnit> teacherUnits = getTeacherCurriculumUnits(teacherId);
        
        System.out.println("[DEBUG] Found " + teacherUnits.size() + " curriculum units for teacher");
        
        // Map to store students and their curriculum units
        Map<Long, TeacherStudentDto> studentMap = new HashMap<>();
        
        for (CurriculumUnit unit : teacherUnits) {
            // Get all enrollments for this curriculum unit
            List<Enrollment> enrollments = enrollmentRepository.findByCurriculumUnitId(unit.getId());
            
            for (Enrollment enrollment : enrollments) {
                Person student = enrollment.getStudent();
                Long studentId = student.getId();
                
                // Create or update student entry
                if (studentMap.containsKey(studentId)) {
                    // Add curriculum unit code to existing student
                    TeacherStudentDto existingDto = studentMap.get(studentId);
                    List<String> updatedCodes = new ArrayList<>(existingDto.curriculumUnitCodes());
                    if (!updatedCodes.contains(unit.getCode())) {
                        updatedCodes.add(unit.getCode());
                    }
                    studentMap.put(studentId, new TeacherStudentDto(
                        existingDto.student(),
                        updatedCodes
                    ));
                } else {
                    // Create new student entry
                    PersonDto studentDto = new PersonDto(student);
                    List<String> codes = new ArrayList<>();
                    codes.add(unit.getCode());
                    studentMap.put(studentId, new TeacherStudentDto(studentDto, codes));
                }
            }
        }
        
        List<TeacherStudentDto> result = new ArrayList<>(studentMap.values());
        System.out.println("[DEBUG] Found " + result.size() + " unique students");
        return result;
    }

    /**
     * Get all assessments with pending grading tasks for the teacher
     * Returns projects and tests that still need grading
     */
    @Transactional(readOnly = true)
    public List<PendingGradingDto> getTeacherPendingGrading(Long teacherId) {
        System.out.println("[DEBUG] Getting pending grading for teacher: " + teacherId);
      
        List<PendingGradingDto> pendingList = new ArrayList<>();
        
        // Get teacher's curriculum units based on role
        List<CurriculumUnit> teacherUnits = getTeacherCurriculumUnits(teacherId);
        
        for (CurriculumUnit unit : teacherUnits) {
            // Check projects for pending grading
            addPendingProjectGrading(unit, pendingList);
            
            // Check tests for pending grading
            addPendingTestGrading(unit, pendingList);
        }
        
        // Sort by pending count (highest first)
        pendingList.sort((a, b) -> Integer.compare(b.pendingCount(), a.pendingCount()));
        
        System.out.println("[DEBUG] Found " + pendingList.size() + " assessments with pending grading");
        return pendingList;
    }

    /**
     * Get teacher statistics including curriculum units, assessments created, and revisions processed
     * Only applies to MAIN_TEACHER type as they can create assessments and process revisions
     */
    @Transactional(readOnly = true)
    public TeacherStatisticsDto getTeacherStatistics(Long teacherId) {
        System.out.println("[DEBUG] Getting statistics for teacher: " + teacherId);
        
        
        Person teacher = personRepository.findById(teacherId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(teacherId)));
    
    
        List<CurriculumUnit> curriculumUnits = curriculumUnitRepository.findByMainTeacherId(teacherId);
        int totalCurriculumUnits = curriculumUnits.size();
        
        // Count projects and tests created across all their curriculum units
        int totalProjects = 0;
        int totalTests = 0;
        int revisionsAccepted = 0;
        int revisionsDenied = 0;
        
        for (CurriculumUnit unit : curriculumUnits) {
            totalProjects += projectRepository.findByCurriculumUnitId(unit.getId()).size();
            
            // Get tests for this curriculum unit
            List<Teste> tests = testeRepository.findByCurriculumUnitId(unit.getId());
            totalTests += tests.size();
            
            // Count revisions for tests in this curriculum unit
            for (Teste test : tests) {
                List<StudentTeste> studentTests = studentTesteRepository.findByTesteId(test.getId());
                for (StudentTeste studentTest : studentTests) {
                    List<Revision> revisions = revisionRepository.findByStudentTesteId(studentTest.getId());
                    for (Revision revision : revisions) {
                        if (revision.getStatus() == Revision.Status.APPROVED) {
                            revisionsAccepted++;
                        } else if (revision.getStatus() == Revision.Status.REJECTED) {
                            revisionsDenied++;
                        }
                    }
                }
            }
        }
        
        
        TeacherStatisticsDto statistics = new TeacherStatisticsDto(
            totalCurriculumUnits,
            totalProjects,
            totalTests,
            revisionsAccepted,
            revisionsDenied
        );
        
        System.out.println("[DEBUG] Teacher statistics: " + statistics);
        return statistics;
    }

    // Private helper methods

    /**
     * Get curriculum units based on teacher type
     * Main teachers: units where they are the main teacher
     * Teaching assistants: units where they are assistants
     */
    private List<CurriculumUnit> getTeacherCurriculumUnits(Long teacherId) {
        
        return curriculumUnitRepository.findByMainTeacherId(teacherId);
    }

    /**
     * Add pending project grading tasks for a curriculum unit
     */
    private void addPendingProjectGrading(CurriculumUnit unit, List<PendingGradingDto> pendingList) {
        List<Project> projects = projectRepository.findByCurriculumUnitId(unit.getId());
        for (Project project : projects) {
            List<StudentGroup> allGroups = studentGroupRepository.findByProjectId(project.getId());
            int totalGroups = allGroups.size();
            int ungradedGroups = (int) allGroups.stream()
                    .filter(group -> group.getGrade() == null)
                    .count();
            
            if (ungradedGroups > 0) {
                pendingList.add(new PendingGradingDto(
                    project.getId(),
                    "PROJECT",
                    project.getTitle(),
                    unit.getCode(),
                    unit.getName(),
                    ungradedGroups,
                    totalGroups
                ));
            }
        }
    }

    /**
     * Add pending test grading tasks for a curriculum unit
     */
    private void addPendingTestGrading(CurriculumUnit unit, List<PendingGradingDto> pendingList) {
        List<Teste> tests = testeRepository.findByCurriculumUnitId(unit.getId());
        for (Teste test : tests) {
            List<StudentTeste> allStudentTests = studentTesteRepository.findByTesteId(test.getId());
            int totalStudents = allStudentTests.size();
            int ungradedStudents = (int) allStudentTests.stream()
                    .filter(studentTest -> studentTest.getGrade() == null)
                    .count();
            
            if (ungradedStudents > 0) {
                pendingList.add(new PendingGradingDto(
                    test.getId(),
                    "TEST",
                    test.getTitle(),
                    unit.getCode(),
                    unit.getName(),
                    ungradedStudents,
                    totalStudents
                ));
            }
        }
    }
}