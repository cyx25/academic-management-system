package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.domain.Enrollment;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.CurriculumUnitDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;

public record EnrollmentDto(
    CurriculumUnitDto curriculumUnit,
    Long id,
    PersonDto student,
    Enrollment.EnrollmentStatus status,
    Integer finalGrade
) {
    public EnrollmentDto(Enrollment enrollment) {
        this(
            new CurriculumUnitDto(enrollment.getCurriculumUnit()),
            enrollment.getId(),
            new PersonDto(enrollment.getStudent()),
            enrollment.getStatus(),
            enrollment.getFinalGrade()
        );
    }
}