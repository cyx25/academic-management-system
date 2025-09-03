package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments;

import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;

public record EnrollmentDto(
    Long id,
    PersonDto student,
    Enrollment.EnrollmentStatus status
) {
    public EnrollmentDto(Enrollment enrollment) {
        this(
            enrollment.getId(),
            new PersonDto(enrollment.getStudent()),
            enrollment.getStatus()
        );
    }
}