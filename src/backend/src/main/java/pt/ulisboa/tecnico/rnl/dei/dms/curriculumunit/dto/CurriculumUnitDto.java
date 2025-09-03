package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.AssistDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.EnrollmentDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.teachings.TeachingDto;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.dto.CourseDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;

import java.util.List;
import java.util.stream.Collectors;

public record CurriculumUnitDto(
    Long id,
    String name,
    String code,
    String semester,
    String ects,
    List<CourseDto> courses,
    PersonDto mainTeacher
  /*   List<AssistDto> assists,
    List<EnrollmentDto> enrollments */
) {
    public CurriculumUnitDto(CurriculumUnit unit) {
        this(
            unit.getId(),
            unit.getName(),
            unit.getCode(),
            unit.getSemester(),
            unit.getEcts(),
            unit.getCourses().stream().map(CourseDto::new).collect(Collectors.toList()),
            unit.getTeaching() != null ? new PersonDto(unit.getTeaching().getTeacher()) : null
           /*  unit.getAssists().stream().map(AssistDto::new).collect(Collectors.toList()),
            unit.getEnrollments().stream().map(EnrollmentDto::new).collect(Collectors.toList()) */
        );
    }
}