package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto;


import java.time.LocalDateTime;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Submission;
import pt.ulisboa.tecnico.rnl.dei.dms.files.FileDto;

public record SubmissionDto(
    Long id,
    FileDto submissionFile,
    float grade,
    LocalDateTime submissionDate,
    String submittedBy
    
) {

    public SubmissionDto(Submission submission) {
        this(
            submission.getId(),
            submission.getSubmissionFile() != null ? new FileDto(submission.getSubmissionFile()) : null,
            submission.getGrade(),
            submission.getSubmissionDate(),
            submission.getStudent() != null ? submission.getStudent().getName() : "Unknown"
        );
    }
    
}
