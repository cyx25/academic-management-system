package pt.ulisboa.tecnico.rnl.dei.dms.courses.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.domain.Course;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.dto.CourseDto;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.repository.CourseRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;


@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;


    private Course fetchCourseOrThrow(long id) {
		return courseRepository.findById(id)
				.orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_COURSE, Long.toString(id)));
	}

    @Transactional
    public List<CourseDto> getCourses() {
        return courseRepository.findAll().stream()
                .map(CourseDto::new)
                .toList();
    }


    @Transactional
	public CourseDto getCourse(long id) {
		return new CourseDto(fetchCourseOrThrow(id));
	}


}
