package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Project;
import pt.ulisboa.tecnico.rnl.dei.dms.files.FileDto;

public record ProjectDto (
    Long id,
    String title,
    Float weight,
    FileDto statementFile,
    LocalDateTime dueDate,
    Integer maxGroupSize
) {
    public ProjectDto(Project project) {
        this(
            project.getId(),
            project.getTitle(),
            project.getWeight(),
            new FileDto(project.getStatementFile()),
            project.getDueDate(),
            project.getMaxGroupSize()
        );
        
    }

}
