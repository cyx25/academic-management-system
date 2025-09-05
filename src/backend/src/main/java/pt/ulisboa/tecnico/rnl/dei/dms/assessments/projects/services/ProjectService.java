package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.services;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Project;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.StudentGroup;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Submission;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto.ProjectDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto.StudentGroupDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto.SubmissionDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.ProjectRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.StudentGroupRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.SubmissionRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.Enrollment;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.repository.CurriculumUnitRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.files.File;
import pt.ulisboa.tecnico.rnl.dei.dms.files.FileRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.files.FileService;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CurriculumUnitRepository curriculumUnitRepository;
    private final PersonRepository personRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final FileRepository assessmentFileRepository;
    private final SubmissionRepository submissionRepository;
    private final FileService fileService;



    public ProjectService(ProjectRepository projectRepository,
                          CurriculumUnitRepository curriculumUnitRepository,
                          PersonRepository personRepository,
                          StudentGroupRepository studentGroupRepository,
                          FileRepository assessmentFileRepository,
                          SubmissionRepository submissionRepository,
                          FileService fileService) {
        this.projectRepository = projectRepository;
        this.curriculumUnitRepository = curriculumUnitRepository;
        this.personRepository = personRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.assessmentFileRepository = assessmentFileRepository;
        this.fileService = fileService;
        this.submissionRepository = submissionRepository;
    }


    @Transactional(readOnly = true)
    public List<ProjectDto> getProjects(Long unitId) {
        return projectRepository.findByCurriculumUnitId(unitId).stream()
                .map(ProjectDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDto createProject(Long unitId, ProjectDto projectDto, MultipartFile file) throws IOException {
        System.out.println("\n--- [DEBUG] Starting project creation ---");
        
        if (file == null || file.isEmpty()) {
            throw new DEIException(ErrorMessage.EMPTY_FILE);
        }

        CurriculumUnit curriculumUnit = curriculumUnitRepository.findById(unitId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_CU));

        // Store file using FileService
        File assessmentFile = fileService.storeFile(file);
        System.out.println("[DEBUG] File stored with path: " + assessmentFile.getPath());

        Project project = new Project(projectDto, curriculumUnit, assessmentFile);
        projectRepository.save(project);

        System.out.println("\n--- [DEBUG] Starting automatic group creation for project: " + project.getTitle() + " ---");
        createStudentGroupsForProject(project, curriculumUnit.getEnrollments());
        System.out.println("--- [DEBUG] Finished automatic group creation ---\n");

        return new ProjectDto(project);
    }


    private void createStudentGroupsForProject(Project project, Set<Enrollment> enrollments) {
        System.out.println("[DEBUG] Found " + enrollments.size() + " enrollments for this curriculum unit.");

        List<Person> students = enrollments.stream()
                .map(Enrollment::getStudent)
                .collect(Collectors.toList());

        System.out.println("[DEBUG] Extracted " + students.size() + " students from enrollments.");

        if (students.isEmpty()) {
            System.out.println("[DEBUG] No students to assign to groups. Aborting group creation.");
            return;
        }

        Collections.shuffle(students);

        int maxGroupSize = project.getMaxGroupSize();
        System.out.println("[DEBUG] Project max group size is set to: " + maxGroupSize);

        if (maxGroupSize <= 0) {
            System.out.println("[DEBUG] Invalid max group size (" + maxGroupSize + "). Aborting group creation.");
            return;
        }

        int groupCounter = 1;

        System.out.println("[DEBUG] Starting loop to create groups...");
        for (int i = 0; i < students.size(); i += maxGroupSize) {
            int end = Math.min(i + maxGroupSize, students.size());
            List<Person> groupMembers = students.subList(i, end);

            StudentGroup studentGroup = new StudentGroup(project, groupCounter++);
            studentGroup.getStudents().addAll(groupMembers);
            studentGroupRepository.save(studentGroup);

            String memberNames = groupMembers.stream().map(Person::getName).collect(Collectors.joining(", "));
            System.out.println("[DEBUG] Created Group " + (groupCounter - 1) + " with members: " + memberNames);
        }
        System.out.println("[DEBUG] Finished loop for group creation.");
    }

    @Transactional
    public ProjectDto updateProject(Long projectId, ProjectDto projectDto, MultipartFile file) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PROJECT));

       
        project.setWeight(projectDto.weight());
        project.setDueDate(projectDto.dueDate());

       
        if (file != null && !file.isEmpty()) {
            File oldFile = project.getStatementFile();
            File newFile = fileService.storeFile(file);
            project.setStatementFile(newFile);
            
      
            if (oldFile != null) {
                fileService.deleteFile(oldFile.getPath());
                assessmentFileRepository.delete(oldFile);
            }
        }

        return new ProjectDto(project);
    }


    @Transactional(readOnly = true)
    public File downloadStatement(long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PROJECT));

        File statementFile = project.getStatementFile();
        if (statementFile == null) {
            throw new DEIException(ErrorMessage.NO_SUCH_FILE);
        }
        return statementFile;
    }


    @Transactional(readOnly = true)
    public File downloadSubmission(long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_SUBMISSION));

        File statementFile = submission.getSubmissionFile();
        if (statementFile == null) {
            throw new DEIException(ErrorMessage.NO_SUCH_FILE);
        }
        return statementFile;

    }

    @Transactional(readOnly = true)
    public List<StudentGroupDto> getProjectGroups(Long projectId) {
        System.out.println("Fetching groups for project ID: " + projectId);
        
        List<StudentGroup> groups = studentGroupRepository.findByProjectId(projectId);
        System.out.println("Found " + groups.size() + " groups for project ID: " + projectId);
        
        return groups.stream()
                .map(group -> {
                    // Get only the latest submission for each group
                    List<SubmissionDto> submissions = group.getSubmissions().stream()
                            .findFirst()
                            .map(SubmissionDto::new)
                            .stream().toList();
                    
                    return new StudentGroupDto(group, submissions);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentGroupDto getMyProjectGroup(Long projectId, Long studentId) {
        StudentGroup group = studentGroupRepository.findByProject_IdAndStudents_Id(projectId, studentId)
                .orElseThrow(() -> new DEIException(ErrorMessage.STUDENT_NOT_IN_GROUP));

        return new StudentGroupDto(group, group.getSubmissions().stream().map(SubmissionDto::new).toList());
    }

    @Transactional
    public void submitToGroup(Long groupId, Long studentId, MultipartFile file) throws IOException {
        StudentGroup group = studentGroupRepository.findById(groupId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_GROUP));

        Person student = personRepository.findById(studentId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON));

        // Check if student is in the group
        if (!group.getStudents().contains(student)) {
            throw new DEIException(ErrorMessage.STUDENT_NOT_IN_GROUP);
        }

        // Check deadline
        if (group.getProject().getDueDate().isBefore(LocalDateTime.now())) {
            throw new DEIException(ErrorMessage.SUBMISSION_DATE_EXPIRED);
        }

        // Store file
        File assessmentFile = fileService.storeFile(file);
        
        // Create submission
        Submission submission = new Submission(group, student, assessmentFile);
        submissionRepository.save(submission);
        
        System.out.println("[DEBUG] Submission created for group " + groupId + " by student " + studentId);
    }

    @Transactional
    public void gradeGroup(Long groupId, Long teacherId, Float grade) {

        StudentGroup group = studentGroupRepository.findById(groupId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_GROUP));
        Person teacher = personRepository.findById(teacherId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON));

        if (grade < 0 || grade > 20) {
            throw new DEIException(ErrorMessage.INVALID_GRADE);
        }
        group.setResponsibleGradedTeacher(teacher);
        group.setGrade(grade);
    }

}
