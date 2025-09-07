package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Project;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.StudentGroup;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.ProjectRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.StudentGroupRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.StudentTeste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.Teste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.StudentTesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.TesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.domain.Revision;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.repository.RevisionRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.domain.Enrollment;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.repository.EnrollmentRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.domain.Course;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.dto.CourseDto;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.repository.CourseRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.AssessmentAverageDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.CurriculumUnitDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.CurriculumUnitStatisticsDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.RevisionStatisticsDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.repository.CurriculumUnitRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;


@Transactional
@Service
public class CurriculumUnitService {

    private final CurriculumUnitRepository curriculumUnitRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProjectRepository projectRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final TesteRepository testeRepository;
    private final StudentTesteRepository studentTesteRepository;
    private final RevisionRepository revisionRepository;
    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;

    public CurriculumUnitService(
            CurriculumUnitRepository curriculumUnitRepository,
            EnrollmentRepository enrollmentRepository,
            ProjectRepository projectRepository,
            StudentGroupRepository studentGroupRepository,
            TesteRepository testeRepository,
            StudentTesteRepository studentTesteRepository,
            RevisionRepository revisionRepository,
            CourseRepository courseRepository,
            PersonRepository personRepository) {
        this.curriculumUnitRepository = curriculumUnitRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.projectRepository = projectRepository;
        this.courseRepository = courseRepository;
        this.personRepository = personRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.testeRepository = testeRepository;
        this.studentTesteRepository = studentTesteRepository;
        this.revisionRepository = revisionRepository;
    }






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

   

   @Transactional
    public void deleteCurriculumUnit(long curriculumUnitId) {
        CurriculumUnit curriculumUnit = fetchCurriculumUnitOrThrow(curriculumUnitId);
     
        for (Course course : curriculumUnit.getCourses()) {
            course.removeCurriculumUnit(curriculumUnit);
            courseRepository.save(course);
        }
        
        curriculumUnitRepository.deleteById(curriculumUnitId);
    }


    @Transactional(readOnly = true)
    public CurriculumUnitStatisticsDto getCurriculumUnitStatistics(Long curriculumUnitId) {
        System.out.println("[DEBUG] Getting statistics for curriculum unit: " + curriculumUnitId);
        
        // Validate curriculum unit exists
        if (!curriculumUnitRepository.existsById(curriculumUnitId)) {
            throw new DEIException(ErrorMessage.NO_SUCH_CU, Long.toString(curriculumUnitId));
        }
        

        List<Integer> finalGrades = getFinalGrades(curriculumUnitId);
      
        List<AssessmentAverageDto> assessmentAverages = getAssessmentAverages(curriculumUnitId);
        

        RevisionStatisticsDto revisionStats = getRevisionStatistics(curriculumUnitId);
        
        CurriculumUnitStatisticsDto statistics = new CurriculumUnitStatisticsDto(
            finalGrades,
            assessmentAverages,
            revisionStats
        );
        
        System.out.println("[DEBUG] Statistics generated for curriculum unit " + curriculumUnitId);
        return statistics;
    }

  
    private List<Integer> getFinalGrades(Long curriculumUnitId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCurriculumUnitId(curriculumUnitId);
        
        return enrollments.stream()
                .map(Enrollment::getFinalGrade)
                .filter(grade -> grade != null)
                .collect(Collectors.toList());
    }

    /**
     * Calculate average grades for all assessments (tests and projects)
     */
    private List<AssessmentAverageDto> getAssessmentAverages(Long curriculumUnitId) {
        List<AssessmentAverageDto> averages = new ArrayList<>();
        
        // Get total enrolled students for percentage calculations
        int totalEnrolledStudents = enrollmentRepository.findByCurriculumUnitId(curriculumUnitId).size();
        
        // Process tests
        List<Teste> tests = testeRepository.findByCurriculumUnitId(curriculumUnitId);
        for (Teste test : tests) {
            List<StudentTeste> studentTests = studentTesteRepository.findByTesteId(test.getId());
            
            List<Float> gradedTests = studentTests.stream()
                    .map(StudentTeste::getGrade)
                    .filter(grade -> grade != null)
                    .collect(Collectors.toList());
            
            Double averageGrade = null;
            if (!gradedTests.isEmpty()) {
                double avg = gradedTests.stream()
                        .mapToDouble(Float::doubleValue)
                        .average()
                        .orElse(0.0);
                averageGrade = BigDecimal.valueOf(avg)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
            }
            
            averages.add(new AssessmentAverageDto(
                "TEST",
                test.getTitle(),
                averageGrade,
                gradedTests.size(),
                totalEnrolledStudents
            ));
        }
        
        // Process projects
        List<Project> projects = projectRepository.findByCurriculumUnitId(curriculumUnitId);
        for (Project project : projects) {
            List<StudentGroup> studentGroups = studentGroupRepository.findByProjectId(project.getId());
            
            List<Float> gradedProjects = studentGroups.stream()
                    .map(StudentGroup::getGrade)
                    .filter(grade -> grade != null)
                    .collect(Collectors.toList());
            
            Double averageGrade = null;
            if (!gradedProjects.isEmpty()) {
                double avg = gradedProjects.stream()
                        .mapToDouble(Float::doubleValue)
                        .average()
                        .orElse(0.0);
                averageGrade = BigDecimal.valueOf(avg)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
            }
            
            // For projects, calculate total students based on group memberships
            int totalStudentsInGroups = studentGroups.stream()
                    .mapToInt(group -> group.getStudents().size())
                    .sum();
            
            averages.add(new AssessmentAverageDto(
                "PROJECT",
                project.getTitle(),
                averageGrade,
                gradedProjects.size(),
                Math.max(totalStudentsInGroups, studentGroups.size()) // Ensure we don't have 0
            ));
        }
        
        return averages;
    }

    /**
     * Calculate revision statistics for all tests in the curriculum unit
     */
    private RevisionStatisticsDto getRevisionStatistics(Long curriculumUnitId) {
        int totalSubmitted = 0;
        int totalApproved = 0;
        int totalDenied = 0;
        int totalPending = 0;
        
        // Get all tests for this curriculum unit
        List<Teste> tests = testeRepository.findByCurriculumUnitId(curriculumUnitId);
        
        for (Teste test : tests) {
            // Get all student tests for this test
            List<StudentTeste> studentTests = studentTesteRepository.findByTesteId(test.getId());
            
            for (StudentTeste studentTest : studentTests) {
                // Each student test can have revisions
                List<Revision> revisions = revisionRepository.findByStudentTesteId(studentTest.getId());
                
                for (Revision revision : revisions) {
                    totalSubmitted++;
                    
                    switch (revision.getStatus()) {
                        case APPROVED:
                            totalApproved++;
                            break;
                        case REJECTED:
                            totalDenied++;
                            break;
                        case PENDING:
                            totalPending++;
                            break;
                        default:
                            // Handle any other status that might exist
                            totalPending++;
                            break;
                    }
                }
            }
        }
        
        return new RevisionStatisticsDto(
            totalSubmitted,
            totalApproved,
            totalDenied,
            totalPending
        );
    }
   

}
