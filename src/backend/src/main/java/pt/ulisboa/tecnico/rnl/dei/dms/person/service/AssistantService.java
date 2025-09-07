package pt.ulisboa.tecnico.rnl.dei.dms.person.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.domain.Assist;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.repository.AssistRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.dto.AssistantGradingTaskDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.dto.AssistantStatisticsDto;


@Service
@Transactional
public class AssistantService {
    
   
    private final AssistRepository assistRepository;
    private final ProjectRepository projectRepository;
    private final TesteRepository testeRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final StudentTesteRepository studentTesteRepository;
    private final RevisionRepository revisionRepository;

    public AssistantService(
           
            AssistRepository assistRepository,
            ProjectRepository projectRepository,
            TesteRepository testeRepository,
            StudentGroupRepository studentGroupRepository,
            StudentTesteRepository studentTesteRepository,
            RevisionRepository revisionRepository) {
        
        this.assistRepository = assistRepository;
        this.projectRepository = projectRepository;
        this.testeRepository = testeRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.studentTesteRepository = studentTesteRepository;
        this.revisionRepository = revisionRepository;
    }

    /**
     * Get grading tasks for assistant teacher
     * Returns tasks with different formats based on due date status
     */
    @Transactional(readOnly = true)
    public List<AssistantGradingTaskDto> getAssistantGradingTasks(Long assistantId) {
        System.out.println("[DEBUG] Getting grading tasks for assistant: " + assistantId);
        
        
        List<AssistantGradingTaskDto> gradingTasks = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
    
        List<Assist> assists = assistRepository.findByAssistantId(assistantId);
        
        for (Assist assist : assists) {
            CurriculumUnit unit = assist.getCurriculumUnit();
            
            // Process projects for this curriculum unit
            List<Project> projects = projectRepository.findByCurriculumUnitId(unit.getId());
            for (Project project : projects) {
                List<StudentGroup> allGroups = studentGroupRepository.findByProjectId(project.getId());
                int totalGroups = allGroups.size();
                int ungradedGroups = (int) allGroups.stream()
                        .filter(group -> group.getGrade() == null)
                        .count();
                
                // Only include if there are groups to grade or if past due with incomplete grading
                if (totalGroups > 0 && (ungradedGroups > 0 || project.getDueDate().isBefore(now))) {
                    boolean isPastDue = project.getDueDate().isBefore(now);
                    
                    gradingTasks.add(new AssistantGradingTaskDto(
                        project.getId(),
                        "PROJECT",
                        project.getTitle(),
                        unit.getCode(),
                        unit.getName(),
                        project.getDueDate(),
                        isPastDue,
                        ungradedGroups,
                        totalGroups
                    ));
                }
            }
            
      
            List<Teste> tests = testeRepository.findByCurriculumUnitId(unit.getId());
            for (Teste test : tests) {
                List<StudentTeste> allStudentTests = studentTesteRepository.findByTesteId(test.getId());
                int totalStudents = allStudentTests.size();
                int ungradedStudents = (int) allStudentTests.stream()
                        .filter(studentTest -> studentTest.getGrade() == null)
                        .count();
                
                // Only include if there are students to grade or if past due with incomplete grading
                if (totalStudents > 0 && (ungradedStudents > 0 || test.getDate().isBefore(now))) {
                    boolean isPastDue = test.getDate().isBefore(now);
                    
                    gradingTasks.add(new AssistantGradingTaskDto(
                        test.getId(),
                        "TEST",
                        test.getTitle(),
                        unit.getCode(),
                        unit.getName(),
                        test.getDate(),
                        isPastDue,
                        ungradedStudents,
                        totalStudents
                    ));
                }
            }
        }
        
        // Sort by priority: past due with pending first, then by due date
        gradingTasks.sort((a, b) -> {
            // High priority tasks first (past due with pending)
            if (a.isPastDue() && a.pendingCount() > 0 && (!b.isPastDue() || b.pendingCount() == 0)) {
                return -1;
            }
            if (b.isPastDue() && b.pendingCount() > 0 && (!a.isPastDue() || a.pendingCount() == 0)) {
                return 1;
            }
            // Then sort by due date
            return a.dueDate().compareTo(b.dueDate());
        });
        
        System.out.println("[DEBUG] Found " + gradingTasks.size() + " grading tasks for assistant");
        return gradingTasks;
    }

    /**
     * Get assistant statistics including curriculum units and grading activities
     */
    @Transactional(readOnly = true)
    public AssistantStatisticsDto getAssistantStatistics(Long assistantId) {
        System.out.println("[DEBUG] Getting statistics for assistant: " + assistantId);
        
      
        // Count curriculum units where assistant works
        List<Assist> assists = assistRepository.findByAssistantId(assistantId);
        int totalCurriculumUnits = assists.size();
        
        int testsGraded = 0;
        int projectsGraded = 0;
        
        // Count graded assessments across all curriculum units
        for (Assist assist : assists) {
            CurriculumUnit unit = assist.getCurriculumUnit();
            
            // Count graded projects
            List<Project> projects = projectRepository.findByCurriculumUnitId(unit.getId());
            for (Project project : projects) {
                List<StudentGroup> gradedGroups = studentGroupRepository.findByProjectIdAndGradeIsNotNull(project.getId());
                projectsGraded += gradedGroups.size();
            }
            
            // Count graded tests
            List<Teste> tests = testeRepository.findByCurriculumUnitId(unit.getId());
            for (Teste test : tests) {
                List<StudentTeste> gradedTests = studentTesteRepository.findByTesteIdAndGradeIsNotNull(test.getId());
                testsGraded += gradedTests.size();
            }
        }
        
        // Count revisions graded by this assistant
        List<Revision> gradedRevisions = revisionRepository.findByReviewedById(
            assistantId
        );
        int revisionsGraded = gradedRevisions.size();
        
        AssistantStatisticsDto statistics = new AssistantStatisticsDto(
            totalCurriculumUnits,
            testsGraded,
            projectsGraded,
            revisionsGraded
        );
        
        System.out.println("[DEBUG] Assistant statistics: " + statistics);
        return statistics;
    }
}