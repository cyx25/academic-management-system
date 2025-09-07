package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.service;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.courses.domain.Course;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.dto.CourseDto;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.repository.CourseRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.CurriculumUnitDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.repository.CurriculumUnitRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;


@Transactional
@Service
public class CurriculumUnitService {

    @Autowired
    private CurriculumUnitRepository curriculumUnitRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CourseRepository courseRepository;


/* 
    @Autowired
    private AssistRepository assistRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;
 */

    private CurriculumUnit fetchCurriculumUnitOrThrow(long id) {
		return curriculumUnitRepository.findById(id)
				.orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_CU, Long.toString(id)));
	}

    private void debugPrintUnits() {
        // print unit courses
        curriculumUnitRepository.findAll().forEach(unit -> {
            System.out.println("Curriculum Unit " + unit.getId() + ": " + unit.getName());
        
            System.out.println("   Courses:");
        unit.getCourses().forEach(course -> {
            System.out.println("     - " + course.getId() + ": " + course.getName());
        });

        });
        // print their dto's and courses
        curriculumUnitRepository.findAll().forEach(unit -> {
            CurriculumUnitDto dto = new CurriculumUnitDto(unit);
            System.out.println("Curriculum Unit DTO " + dto.id() + ": " + dto.name());
            System.out.println("   Courses:");
            dto.courses().forEach(course -> {
                System.out.println("     - " + course.id() + ": " + course.name());
            });
        });
    }

    @Transactional
    public List<CurriculumUnitDto> getCurriculumUnits() {

        debugPrintUnits();

        return curriculumUnitRepository.findAll().stream()
                .map(CurriculumUnitDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CurriculumUnitDto saveCurriculumUnit(Long id, CurriculumUnitDto curriculumUnitDto) {
        Person mainTeacher = fetchMainTeacher(curriculumUnitDto.mainTeacher().id());
        Set<Course> courses = fetchCourses(curriculumUnitDto.courses());
              
        CurriculumUnit curriculumUnit;
        if (id == null) {
            // Create new
            curriculumUnit = new CurriculumUnit(curriculumUnitDto, mainTeacher, courses);
        } else {
            // Update existing
            curriculumUnit = fetchCurriculumUnitOrThrow(id);
            updateCurriculumUnitFields(curriculumUnit, curriculumUnitDto, mainTeacher, courses);
        }
        
        curriculumUnit = curriculumUnitRepository.save(curriculumUnit);
        updateBidirectionalRelationships(curriculumUnit, courses);
        
        return new CurriculumUnitDto(curriculumUnit);
    }

    private Person fetchMainTeacher(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, id.toString()));
    }

    private Set<Course> fetchCourses(List<CourseDto> courseDtos) {
        Set<Course> courses = new HashSet<>();
        if (courseDtos != null) {
            for (CourseDto courseDto : courseDtos) {
                Course course = courseRepository.findById(courseDto.id())
                        .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_COURSE, courseDto.id().toString()));
                courses.add(course);
            }
        }
        return courses;
    }

    private void updateCurriculumUnitFields(CurriculumUnit curriculumUnit, CurriculumUnitDto dto, Person mainTeacher, Set<Course> courses) {
        Set<Course> oldCourses = new HashSet<>(curriculumUnit.getCourses());
        
        curriculumUnit.setName(dto.name());
        curriculumUnit.setCode(dto.code());
        curriculumUnit.setSemester(dto.semester());
        curriculumUnit.setEcts(dto.ects());
        curriculumUnit.setMainTeacher(mainTeacher);
        curriculumUnit.setCourses(courses);
        
        removeFromOldCourses(curriculumUnit, oldCourses, courses);
    }

    private void removeFromOldCourses(CurriculumUnit curriculumUnit, Set<Course> oldCourses, Set<Course> newCourses) {
        for (Course oldCourse : oldCourses) {
            if (!newCourses.contains(oldCourse)) {
                oldCourse.getCurriculumUnits().remove(curriculumUnit);
                courseRepository.save(oldCourse);
            }
        }
    }

    private void updateBidirectionalRelationships(CurriculumUnit curriculumUnit, Set<Course> courses) {
        for (Course course : courses) {
            if (!course.getCurriculumUnits().contains(curriculumUnit)) {
                course.getCurriculumUnits().add(curriculumUnit);
                courseRepository.save(course);
            }
        }
    }

    @Transactional
    public CurriculumUnitDto createCurriculumUnit(CurriculumUnitDto curriculumUnitDto) {
        validateCurriculumUnitDto(curriculumUnitDto, null);
        return saveCurriculumUnit(null, curriculumUnitDto);
    }

    @Transactional
	public CurriculumUnitDto getCurriculumUnit(long id) {
        // debug what is being sent
		return new CurriculumUnitDto(fetchCurriculumUnitOrThrow(id));
	}

    @Transactional
    public CurriculumUnitDto updateCurriculumUnit(long curriculumUnitId, CurriculumUnitDto curriculumUnitDto) {
        validateCurriculumUnitDto(curriculumUnitDto, curriculumUnitId);
        return saveCurriculumUnit(curriculumUnitId, curriculumUnitDto);
    }


    private void validateCurriculumUnitDto(CurriculumUnitDto curriculumUnitDto, Long id) {
        
        if (curriculumUnitDto.name() == null || curriculumUnitDto.name().trim().isEmpty()) {
        throw new DEIException(ErrorMessage.CU_NAME_REQUIRED);
        }
        if (curriculumUnitDto.code() == null || curriculumUnitDto.code().trim().isEmpty()) {
            throw new DEIException(ErrorMessage.CU_CODE_REQUIRED);   
        }

        if(curriculumUnitDto.code().length() > 10) {
			throw new DEIException(ErrorMessage.CU_CODE_TOO_LONG);
		}
		if(!curriculumUnitDto.code().matches("\\S+")) {
			throw new DEIException(ErrorMessage.CU_CODE_NOT_VALID);
		}

        if (curriculumUnitDto.semester() == null) {
            throw new DEIException(ErrorMessage.CU_SEMESTER_REQUIRED);
        }
        if (curriculumUnitDto.ects() == null) {
            throw new DEIException(ErrorMessage.CU_ECTS_REQUIRED);
        }
        if (curriculumUnitDto.mainTeacher() == null) {
            throw new DEIException(ErrorMessage.CU_MAIN_TEACHER_REQUIRED);
        }
        int cu_semester;
        try {
            cu_semester = Integer.parseInt(curriculumUnitDto.semester());
        } catch (NumberFormatException e) {
            throw new DEIException(ErrorMessage.CU_INVALID_SEMESTER);
        }
        if (cu_semester != 1 && cu_semester != 2) {
            throw new DEIException(ErrorMessage.CU_INVALID_SEMESTER);
        }

        int cu_ects;
        try {
            cu_ects = Integer.parseInt(curriculumUnitDto.ects());
        } catch (NumberFormatException e) {
            throw new DEIException(ErrorMessage.CU_INVALID_ECTS);
        }
        if (cu_ects < 1 || cu_ects > 12) {
            throw new DEIException(ErrorMessage.CU_INVALID_ECTS);
        }

        // Validate code uniqueness
		if (curriculumUnitDto.code() != null && !curriculumUnitDto.code().isEmpty()) {
			CurriculumUnit existingUnit = curriculumUnitRepository.findByCode(curriculumUnitDto.code());
			if (existingUnit != null) {
				// For updates, allow if it's the same unit
				if (id == null || !existingUnit.getId().equals(id)) {
					throw new DEIException(ErrorMessage.CU_CODE_ALREADY_EXISTS, curriculumUnitDto.code());
				}
			}
		}
    }

    // !talvez implementar se tiver tempo
   /*  @Transactional
    public List<CurriculumUnitDto> getCurriculumUnitsByPerson(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON));

        List<CurriculumUnit> curriculumUnits = new ArrayList<>();

        switch (person.getType().toString()) {
            case "TEACHER":
                curriculumUnits.addAll(
                        teachingRepository.findByTeacherId(personId).stream()
                                .map(Teaching::getCurriculumUnit)
                                .toList()
                );
                // A teacher can also be an assistant in other CUs
            case "TEACHING_ASSISTANT":
                curriculumUnits.addAll(
                        assistRepository.findByAssistantId(personId).stream()
                                .map(Assist::getCurriculumUnit)
                                .toList()
                );
                break;
            case "STUDENT":
                curriculumUnits.addAll(
                        enrollmentRepository.findByStudentId(personId).stream()
                                .map(Enrollment::getCurriculumUnit)
                                .toList()
                );
                break;
            default:
                
                break;
        }

        // Remove duplicates in case a person is both a main teacher and an assistant
        return curriculumUnits.stream()
                .map(CurriculumUnitDto::new)
                .collect(Collectors.toList());
    }
 */

   @Transactional
    public void deleteCurriculumUnit(long curriculumUnitId) {
        CurriculumUnit curriculumUnit = fetchCurriculumUnitOrThrow(curriculumUnitId);
     
        for (Course course : curriculumUnit.getCourses()) {
            course.removeCurriculumUnit(curriculumUnit);
            courseRepository.save(course);
        }
        
        curriculumUnitRepository.deleteById(curriculumUnitId);
    }

   

}
