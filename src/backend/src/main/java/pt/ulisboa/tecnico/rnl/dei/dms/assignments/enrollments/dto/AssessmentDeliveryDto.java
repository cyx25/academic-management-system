package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto;

import java.time.LocalDateTime;

public record AssessmentDeliveryDto(
    LocalDateTime submissionTime, // both projects and tests
    String curriculumUnitName,
    String assessmentType
 
) {
    
}
