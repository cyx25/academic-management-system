package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto;

/**
 * DTO for revision statistics
 */
public record RevisionStatisticsDto(
    Integer totalSubmitted,
    Integer totalApproved,
    Integer totalDenied,
    Integer totalPending
) {
    
    // Validation in constructor
    public RevisionStatisticsDto {
        if (totalSubmitted == null || totalSubmitted < 0) {
            throw new IllegalArgumentException("Total submitted cannot be null or negative");
        }
        if (totalApproved == null || totalApproved < 0) {
            throw new IllegalArgumentException("Total approved cannot be null or negative");
        }
        if (totalDenied == null || totalDenied < 0) {
            throw new IllegalArgumentException("Total denied cannot be null or negative");
        }
        if (totalPending == null || totalPending < 0) {
            throw new IllegalArgumentException("Total pending cannot be null or negative");
        }
        if (totalApproved + totalDenied + totalPending != totalSubmitted) {
            throw new IllegalArgumentException("Sum of approved, denied and pending must equal total submitted");
        }
    }
    
    /**
     * Calculate approval rate
     */
    public Double getApprovalRate() {
        if (totalSubmitted == 0) return null;
        return ((double) totalApproved / totalSubmitted) * 100.0;
    }
    
    /**
     * Calculate denial rate
     */
    public Double getDenialRate() {
        if (totalSubmitted == 0) return null;
        return ((double) totalDenied / totalSubmitted) * 100.0;
    }
    
    /**
     * Check if there are pending revisions
     */
    public Boolean hasPendingRevisions() {
        return totalPending > 0;
    }
}