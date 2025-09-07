package pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.dto;

import java.time.LocalDateTime;

/**
 * DTO for representing grading tasks for assistant teachers
 * Can represent either pending grading (past due) or upcoming deadlines
 */
public record AssistantGradingTaskDto(
    Long assessmentId,
    String assessmentType, // "TEST" or "PROJECT"
    String assessmentName,
    String curriculumUnitCode,
    String curriculumUnitName,
    LocalDateTime dueDate,
    Boolean isPastDue,
    Integer pendingCount,
    Integer totalCount
) {
    
    // Validation in constructor
    public AssistantGradingTaskDto {
        if (assessmentId == null || assessmentId <= 0) {
            throw new IllegalArgumentException("Assessment ID cannot be null or negative");
        }
        if (assessmentType == null || (!assessmentType.equals("TEST") && !assessmentType.equals("PROJECT"))) {
            throw new IllegalArgumentException("Assessment type must be TEST or PROJECT");
        }
        if (assessmentName == null || assessmentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Assessment name cannot be null or empty");
        }
        if (curriculumUnitCode == null || curriculumUnitCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Curriculum unit code cannot be null or empty");
        }
        if (curriculumUnitName == null || curriculumUnitName.trim().isEmpty()) {
            throw new IllegalArgumentException("Curriculum unit name cannot be null or empty");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }
        if (isPastDue == null) {
            throw new IllegalArgumentException("isPastDue cannot be null");
        }
        if (pendingCount == null || pendingCount < 0) {
            throw new IllegalArgumentException("Pending count cannot be null or negative");
        }
        if (totalCount == null || totalCount < 0) {
            throw new IllegalArgumentException("Total count cannot be null or negative");
        }
        if (pendingCount > totalCount) {
            throw new IllegalArgumentException("Pending count cannot exceed total count");
        }
    }
    
    /**
     * Calculate completion percentage
     */
    public double getCompletionPercentage() {
        if (totalCount == 0) return 100.0;
        return ((double) (totalCount - pendingCount) / totalCount) * 100.0;
    }
    
    /**
     * Check if grading is complete
     */
    public boolean isComplete() {
        return pendingCount == 0;
    }
    
    /**
     * Get priority level based on due date and completion
     */
    public String getPriorityLevel() {
        if (isPastDue && pendingCount > 0) return "HIGH";
        if (isPastDue && pendingCount == 0) return "COMPLETED";
        if (dueDate.isBefore(LocalDateTime.now().plusDays(3)) && pendingCount > 0) return "MEDIUM";
        return "LOW";
    }
}