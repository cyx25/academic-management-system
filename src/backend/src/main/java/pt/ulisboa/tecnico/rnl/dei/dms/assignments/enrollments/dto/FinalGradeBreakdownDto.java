package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto;

import java.util.List;

public record FinalGradeBreakdownDto(
    Long studentId,
    String studentName,
    Long curriculumUnitId,
    String curriculumUnitName,
    List<AssessmentDto> assessments,
    Integer totalWeight,        // Sum of all assignment weights
    Integer finalGrade,         // Final calculated grade
    boolean isComplete          // Whether all assignments are graded and total weight = 100
) {
    
    public FinalGradeBreakdownDto {
        // Basic validation
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        if (curriculumUnitId == null) {
            throw new IllegalArgumentException("Curriculum Unit ID cannot be null");
        }
        if (assessments == null) {
            throw new IllegalArgumentException("Assignments list cannot be null");
        }
        if (totalWeight == null || totalWeight < 0) {
            throw new IllegalArgumentException("Total weight must be non-negative");
        }
    }
}