package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto;

/**
 * DTO for grade distribution statistics
 */
public record GradeDistributionDto(
    Integer excellent,     // 18-20
    Integer veryGood,      // 16-17
    Integer good,          // 14-15
    Integer satisfactory,  // 10-13
    Integer insufficient   // 0-9
) {
    
    // Validation in constructor
    public GradeDistributionDto {
        if (excellent == null || excellent < 0) {
            throw new IllegalArgumentException("Excellent count cannot be null or negative");
        }
        if (veryGood == null || veryGood < 0) {
            throw new IllegalArgumentException("Very good count cannot be null or negative");
        }
        if (good == null || good < 0) {
            throw new IllegalArgumentException("Good count cannot be null or negative");
        }
        if (satisfactory == null || satisfactory < 0) {
            throw new IllegalArgumentException("Satisfactory count cannot be null or negative");
        }
        if (insufficient == null || insufficient < 0) {
            throw new IllegalArgumentException("Insufficient count cannot be null or negative");
        }
    }
    
    /**
     * Get total number of graded students
     */
    public Integer getTotalGraded() {
        return excellent + veryGood + good + satisfactory + insufficient;
    }
    
    /**
     * Get passing rate (grades >= 10)
     */
    public Double getPassingRate() {
        int totalGraded = getTotalGraded();
        if (totalGraded == 0) return null;
        int passing = excellent + veryGood + good + satisfactory;
        return ((double) passing / totalGraded) * 100.0;
    }
}