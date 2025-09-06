package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto;

public record AssessmentDto(
    String type,           // "TEST" or "PROJECT"
    String name,           // Assessmentt name
    Integer weight,        // Weight percentage
    Float grade           // Student's grade (null if not graded)
) {
    
    public AssessmentDto {
        // Validation
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Assignment type cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Assignment name cannot be null or empty");
        }
        if (weight == null || weight < 0 || weight > 100) {
            throw new IllegalArgumentException("Weight must be between 0 and 100");
        }
        // Grade can be null (not graded yet), but if present, should be valid
        if (grade != null && (grade < 0 || grade > 20)) {
            throw new IllegalArgumentException("Grade must be between 0 and 20");
        }
    }
}