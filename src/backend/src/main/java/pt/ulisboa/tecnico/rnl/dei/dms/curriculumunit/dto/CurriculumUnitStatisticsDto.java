package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto;

import java.util.List;

/**
 * DTO for curriculum unit advanced statistics
 */
public record CurriculumUnitStatisticsDto(
    List<Integer> finalGrades,
    List<AssessmentAverageDto> assessmentAverages,
    RevisionStatisticsDto revisionStatistics
) {
    
    // Validation in constructor
    public CurriculumUnitStatisticsDto {
        if (finalGrades == null) {
            throw new IllegalArgumentException("Final grades cannot be null");
        }
        if (assessmentAverages == null) {
            throw new IllegalArgumentException("Assessment averages cannot be null");
        }
        if (revisionStatistics == null) {
            throw new IllegalArgumentException("Revision statistics cannot be null");
        }
    }
    
    /**
     * Calculate overall average grade
     */
    public Double getOverallAverage() {
        if (finalGrades.isEmpty()) return null;
        return finalGrades.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Get grade distribution statistics
     */
    public GradeDistributionDto getGradeDistribution() {
        if (finalGrades.isEmpty()) {
            return new GradeDistributionDto(0, 0, 0, 0, 0);
        }
        
        int excellent = (int) finalGrades.stream().filter(grade -> grade >= 18).count();
        int veryGood = (int) finalGrades.stream().filter(grade -> grade >= 16 && grade < 18).count();
        int good = (int) finalGrades.stream().filter(grade -> grade >= 14 && grade < 16).count();
        int satisfactory = (int) finalGrades.stream().filter(grade -> grade >= 10 && grade < 14).count();
        int insufficient = (int) finalGrades.stream().filter(grade -> grade < 10).count();
        
        return new GradeDistributionDto(excellent, veryGood, good, satisfactory, insufficient);
    }
}