package pt.ulisboa.tecnico.rnl.dei.dms.courses.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.courses.domain.Course;

public record CourseDto(long id, String name, String code, int duration) {

	public CourseDto(Course course) {
		this(course.getId(), course.getName(), course.getCode(),
				course.getDuration());
	}

	
}