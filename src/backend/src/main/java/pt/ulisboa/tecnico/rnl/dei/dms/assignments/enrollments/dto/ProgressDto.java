package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto;

public record ProgressDto(
    Integer totalProjects,
    Integer completedProjects,
    Integer totalTests,
    Integer completedTests,
    Float averageGrade,
    Integer totalCurriculumUnits,
    Integer completedCurriculumUnits

) {
    
}
