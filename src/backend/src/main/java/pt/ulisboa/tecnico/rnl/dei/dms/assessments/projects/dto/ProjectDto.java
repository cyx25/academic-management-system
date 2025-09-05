package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.files.AssessmentFileDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Project;

public record ProjectDto (
    Long id,
    String title,
    Float weight,
    AssessmentFileDto file,
    LocalDateTime dueDate,
    Integer maxGroupSize
) {
    public ProjectDto(Project project) {
        this(
            project.getId(),
            project.getTitle(),
            project.getWeight(),
            new AssessmentFileDto(project.getStatementFile()),
            project.getDueDate(),
            project.getMaxGroupSize()
        );
        
    }

}
