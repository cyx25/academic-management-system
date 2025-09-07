package pt.ulisboa.tecnico.rnl.dei.dms.assignments.teachings.dto;


public record PendingGradingDto(
    Long assessmentId,
    String assessmentType, // "TEST" or "PROJECT"
    String assessmentName,
    String curriculumUnitCode,
    String curriculumUnitName,
    Integer pendingCount,
    Integer totalCount
) {
    
    // Validation in constructor
    public PendingGradingDto {
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
    
    
    public double getCompletionPercentage() {
        if (totalCount == 0) return 100.0;
        return ((double) (totalCount - pendingCount) / totalCount) * 100.0;
    }
    
   
    public boolean isComplete() {
        return pendingCount == 0;
    }
}