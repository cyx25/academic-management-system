package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto.GradingDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto.ProjectDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto.StudentGroupDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.services.ProjectService;
import pt.ulisboa.tecnico.rnl.dei.dms.files.File;
import pt.ulisboa.tecnico.rnl.dei.dms.files.FileService;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import java.util.List;

@RestController
public class ProjectController {

    private final ProjectService projectService;
    private final FileService fileService;

    public ProjectController(ProjectService projectService, FileService fileService) {
        this.projectService = projectService;
        this.fileService = fileService;
    }


    @GetMapping("/curriculum-units/{unitId}/projects")
    public List<ProjectDto> getProjects(@PathVariable Long unitId) {
        return projectService.getProjects(unitId);
    }

    @PostMapping(value = "/curriculum-units/{unitId}/projects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProjectDto createProject(@PathVariable Long unitId,
                                     @RequestPart("project") ProjectDto projectDto,
                                     @RequestPart("file") MultipartFile file) throws IOException {
        System.out.println("File received: " + (file != null ? file.getOriginalFilename() : "null"));
        
        return projectService.createProject(unitId, projectDto, file);
    }
        

    @PutMapping(value = "/projects/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ProjectDto updateProject(@PathVariable Long projectId,
                                        @RequestPart("project") ProjectDto projectDto,
                                        @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
            return projectService.updateProject(projectId, projectDto, file);
    }

    @GetMapping("/projects/{projectId}/statement")
    public ResponseEntity<Resource> downloadStatement(@PathVariable long projectId) throws IOException {
        File file = projectService.downloadStatement(projectId);
        Resource resource = fileService.loadFileAsResource(file.getPath());
        return buildFileResponse(file, resource);
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable long fileId) throws IOException {
        File file = fileService.getFileById(fileId);
        Resource resource = fileService.loadFileAsResource(file.getPath());
        return buildFileResponse(file, resource);
    }

    
    private ResponseEntity<Resource> buildFileResponse(File file, Resource resource) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }


    
    @GetMapping("/projects/submissions/{submissionId}")
    public ResponseEntity<Resource> downloadSubmission(@PathVariable long submissionId) throws IOException {
        File file = projectService.downloadSubmission(submissionId);
        Resource resource = fileService.loadFileAsResource(file.getPath());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/projects/{projectId}")
    public List<StudentGroupDto> getProjectGroups(@PathVariable long projectId) {
        return projectService.getProjectGroups(projectId);
    }


    @GetMapping("/projects/{projectId}/{studentId}")
    public StudentGroupDto getMyProjectGroup(@PathVariable long projectId, @PathVariable long studentId) {
        return projectService.getMyProjectGroup(projectId, studentId);
    }

    @PostMapping(value = "/groups/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void submitToGroup(@RequestParam long groupId, 
                              @RequestParam long studentId, 
                              @RequestPart("file") MultipartFile file) throws IOException {
        projectService.submitToGroup(groupId, studentId, file);
    }

    @PutMapping("/groups/grade")
    public void gradeGroup(@RequestBody GradingDto gradingDto) {
        projectService.gradeGroup(gradingDto.groupId(), gradingDto.teacherId(), gradingDto.grade());
    }



}