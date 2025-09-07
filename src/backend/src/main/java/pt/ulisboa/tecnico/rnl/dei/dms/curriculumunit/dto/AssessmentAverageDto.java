package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto;

/**
 * DTO for assessment average grades
 */
public record AssessmentAverageDto(
    String assessmentType, // "TEST" or "PROJECT"
    String assessmentTitle,
    Double averageGrade,
    Integer totalGraded,
    Integer totalStudents
) {
    
    // Validation in constructor
    public AssessmentAverageDto {
        if (assessmentType == null || (!assessmentType.equals("TEST") && !assessmentType.equals("PROJECT"))) {
            throw new IllegalArgumentException("Assessment type must be TEST or PROJECT");
        }
        if (assessmentTitle == null || assessmentTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Assessment title cannot be null or empty");
        }
        if (averageGrade != null && (averageGrade < 0 || averageGrade > 20)) {
            throw new IllegalArgumentException("Average grade must be between 0 and 20");
        }
        if (totalGraded == null || totalGraded < 0) {
            throw new IllegalArgumentException("Total graded cannot be null or negative");
        }
        if (totalStudents == null || totalStudents < 0) {
            throw new IllegalArgumentException("Total students cannot be null or negative");
        }
        if (totalGraded > totalStudents) {
            throw new IllegalArgumentException("Total graded cannot exceed total students");
        }
    }
    
    /**
     * Calculate completion percentage
     */
    public Double getCompletionPercentage() {
        if (totalStudents == 0) return 100.0;
        return ((double) totalGraded / totalStudents) * 100.0;
    }
    
    /**
     * Check if assessment is fully graded
     */
    public Boolean isFullyGraded() {
        return totalGraded.equals(totalStudents);
    }
}