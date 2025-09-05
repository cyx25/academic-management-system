package pt.ulisboa.tecnico.rnl.dei.dms.courses.dto;

import java.util.ArrayList;
import java.util.List;


import pt.ulisboa.tecnico.rnl.dei.dms.courses.domain.Course;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.SimpleCurriculumUnitDto;

public record CourseDto(
    Long id,
    String name,
    String code,
    String duration,
    List<SimpleCurriculumUnitDto> curriculumUnits
) {
    public CourseDto(Course course) {
        this(
            course.getId(),
            course.getName(),
            course.getCode(),
            course.getDuration(),
            course.getCurriculumUnits() != null ? 
                course.getCurriculumUnits().stream()
                    .map(SimpleCurriculumUnitDto::new)
                    .toList() : 
                new ArrayList<>()
        );
    }
}