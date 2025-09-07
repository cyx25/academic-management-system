package pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.dto;

/**
 * DTO for representing assistant teacher statistics and performance metrics
 */
public record AssistantStatisticsDto(
    Integer totalCurriculumUnits,
    Integer testsGraded,
    Integer projectsGraded,
    Integer revisionsGraded
) {
    
    // Validation in constructor
    public AssistantStatisticsDto {
        if (totalCurriculumUnits == null || totalCurriculumUnits < 0) {
            throw new IllegalArgumentException("Total curriculum units cannot be null or negative");
        }
        if (testsGraded == null || testsGraded < 0) {
            throw new IllegalArgumentException("Tests graded cannot be null or negative");
        }
        if (projectsGraded == null || projectsGraded < 0) {
            throw new IllegalArgumentException("Projects graded cannot be null or negative");
        }
        if (revisionsGraded == null || revisionsGraded < 0) {
            throw new IllegalArgumentException("Revisions graded cannot be null or negative");
        }
    }
    
    /**
     * Get total assessments graded (tests + projects)
     */
    public Integer getTotalAssessmentsGraded() {
        return testsGraded + projectsGraded;
    }
    
    /**
     * Get total grading activities (assessments + revisions)
     */
    public Integer getTotalGradingActivities() {
        return testsGraded + projectsGraded + revisionsGraded;
    }
    
    /**
     * Calculate average grading activities per curriculum unit
     */
    public double getAverageGradingPerUnit() {
        if (totalCurriculumUnits == 0) return 0.0;
        return (double) getTotalGradingActivities() / totalCurriculumUnits;
    }
}