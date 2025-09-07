package pt.ulisboa.tecnico.rnl.dei.dms.assignments.teachings.dto;


public record TeacherStatisticsDto(
    Integer totalCurriculumUnits,
    Integer totalProjectsCreated,
    Integer totalTestsCreated,
    Integer revisionsAccepted,
    Integer revisionsDenied
) {
    
    // Validation in constructor
    public TeacherStatisticsDto {
        if (totalCurriculumUnits == null || totalCurriculumUnits < 0) {
            throw new IllegalArgumentException("Total curriculum units cannot be null or negative");
        }
        if (totalProjectsCreated == null || totalProjectsCreated < 0) {
            throw new IllegalArgumentException("Total projects created cannot be null or negative");
        }
        if (totalTestsCreated == null || totalTestsCreated < 0) {
            throw new IllegalArgumentException("Total tests created cannot be null or negative");
        }
        if (revisionsAccepted == null || revisionsAccepted < 0) {
            throw new IllegalArgumentException("Revisions accepted cannot be null or negative");
        }
        if (revisionsDenied == null || revisionsDenied < 0) {
            throw new IllegalArgumentException("Revisions denied cannot be null or negative");
        }
    }
    
    /**
     * Get total assessments created (projects + tests)
     */
    public Integer getTotalAssessments() {
        return totalProjectsCreated + totalTestsCreated;
    }
    
    /**
     * Get total revisions processed
     */
    public Integer getTotalRevisionsProcessed() {
        return revisionsAccepted + revisionsDenied;
    }
    
    /**
     * Calculate revision acceptance rate
     */
    public double getRevisionAcceptanceRate() {
        int total = getTotalRevisionsProcessed();
        if (total == 0) return 0.0;
        return ((double) revisionsAccepted / total) * 100.0;
    }
}