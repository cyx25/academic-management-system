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

    @Transactional
	public CourseDto createCourse(CourseDto courseDto) {
		validateCourseDto(courseDto, null);
		return saveCourseDto(null, courseDto);
	}

	@Transactional
	public CourseDto updateCourse(long id, CourseDto courseDto) {
		validateCourseDto(courseDto, id);
		fetchCourseOrThrow(id); // ensure exists
		return saveCourseDto(id, courseDto);
	}

	private CourseDto saveCourseDto(Long id, CourseDto courseDto) {
		Course course = new Course(courseDto);
		course.setId(id); // null for create, actual id for update
		return new CourseDto(courseRepository.save(course));
	}

	private void validateCourseDto(CourseDto courseDto, Long id) {
		
		if (courseDto.name() == null || courseDto.name().trim().isEmpty()) {
        throw new DEIException(ErrorMessage.COURSE_NAME_REQUIRED);
		}
		if (courseDto.code() == null || courseDto.code().trim().isEmpty()) {
			throw new DEIException(ErrorMessage.COURSE_CODE_REQUIRED);
		}

		if (courseDto.duration() == null || courseDto.duration().trim().isEmpty()) {
			throw new DEIException(ErrorMessage.COURSE_DURATION_REQUIRED);
		}
		
		int course_duration;
		try {
			course_duration = Integer.parseInt(courseDto.duration());
		} catch (NumberFormatException e) {
			throw new DEIException(ErrorMessage.INVALID_COURSE_DURATION);
		}

		if (course_duration < 1 || course_duration > 5) {
			throw new DEIException(ErrorMessage.INVALID_COURSE_DURATION);
		}

		// Validate code uniqueness
		if (courseDto.code() != null && !courseDto.code().isEmpty()) {
			Course existingCourse = courseRepository.findByCode(courseDto.code());
			if (existingCourse != null) {
				// For updates, allow if it's the same course
				if (id == null || !existingCourse.getId().equals(id)) {
					throw new DEIException(ErrorMessage.COURSE_CODE_ALREADY_EXISTS, courseDto.code());
				}
			}
		}
	}

    @Transactional
	public void deleteCourse(long id) {
		fetchCourseOrThrow(id); // ensure exists
		courseRepository.deleteById(id);
	}

}
