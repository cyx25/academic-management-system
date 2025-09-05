package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto;


import java.time.LocalDateTime;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.files.AssessmentFileDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Submission;

public record SubmissionDto(
    Long id,
    AssessmentFileDto file,
    float grade,
    LocalDateTime submissionTime,
    String studentName
    
) {

    public SubmissionDto(Submission submission) {
        this(
            submission.getId(),
            new AssessmentFileDto(submission.getSubmissionFile()),
            submission.getGrade(),
            submission.getSubmissionTime(),
            submission.getStudent().getName()
        );
    }
    
}
