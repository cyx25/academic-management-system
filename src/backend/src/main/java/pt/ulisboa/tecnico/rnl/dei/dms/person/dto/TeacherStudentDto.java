package pt.ulisboa.tecnico.rnl.dei.dms.person.dto;

import java.util.List;


public record TeacherStudentDto(
    PersonDto student,
    List<String> curriculumUnitCodes
) {
    
    // Validation in constructor
    public TeacherStudentDto {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (curriculumUnitCodes == null) {
            curriculumUnitCodes = List.of();
        }
    }
    

    public String getFormattedCurriculumUnits() {
        return String.join(", ", curriculumUnitCodes);
    }
}