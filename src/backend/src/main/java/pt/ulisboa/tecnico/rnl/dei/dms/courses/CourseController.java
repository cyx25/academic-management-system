package pt.ulisboa.tecnico.rnl.dei.dms.courses;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.rnl.dei.dms.courses.dto.CourseDto;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.service.CourseService;


@RestController
public class CourseController {
	@Autowired
	private CourseService courseService;

	@GetMapping("/courses")
	public List<CourseDto> getCourses() {
		return courseService.getCourses();
	}

	@GetMapping("/courses/{id}")
	public CourseDto getCourse(@PathVariable long id) {
		return courseService.getCourse(id);
	}


}