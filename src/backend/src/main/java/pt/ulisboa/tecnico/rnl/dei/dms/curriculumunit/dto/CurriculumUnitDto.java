package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto;

import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.dto.CourseDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;

public record CurriculumUnitDto(
    long id,
    String name,
    String code,
    int semester,
    int ects,
    PersonDto mainTeacher,
    List<CourseDto> courses
) {
    public CurriculumUnitDto(CurriculumUnit curriculumUnit) {
        this(
            curriculumUnit.getId(),
            curriculumUnit.getName(),
            curriculumUnit.getCode(),
            curriculumUnit.getSemester(),
            curriculumUnit.getEcts(),
            new PersonDto(curriculumUnit.getMainTeacher()),
            curriculumUnit.getCourses().stream()
                .map(CourseDto::new)
                .collect(Collectors.toList())
        );
    }
}