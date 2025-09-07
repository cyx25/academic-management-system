package pt.ulisboa.tecnico.rnl.dei.dms.person;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.AssessmentDeliveryDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.ProgressDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.UnitFinalGradeDto;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person.PersonType;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.service.PersonService;
import pt.ulisboa.tecnico.rnl.dei.dms.person.service.StudentService;



@RestController
public class PersonController {

	@Autowired
	private PersonService personService;

	@Autowired
    private StudentService studentService;

	@GetMapping("/people")
	public List<PersonDto> getPeople() {
		return personService.getPeople();
	}

	@PostMapping("/people")
	public PersonDto createPerson(@RequestBody PersonDto personDto) {
		return personService.createPerson(personDto);
	}

	@GetMapping("/people/{id}")
	public PersonDto getPerson(@PathVariable long id) {
		return personService.getPerson(id);
	}

	@PutMapping("/people/{id}")
	public PersonDto updatePerson(@PathVariable long id, @RequestBody PersonDto personDto) {
		return personService.updatePerson(id, personDto);
	}

	@DeleteMapping("/people/{id}")
	public void deletePerson(@PathVariable long id) {
		personService.deletePerson(id);
	}

	@GetMapping("/people/main-teachers")
	public List<PersonDto> getTeachers() {

		return personService.getPeopleByType(PersonType.MAIN_TEACHER);
	}

	@GetMapping("/people/assistant-teachers")
	public List<PersonDto> getAssistantTeachers() {
		return personService.getPeopleByType(PersonType.TEACHING_ASSISTANT);
	}

	@GetMapping("/people/students")
	public List<PersonDto> getStudents() {
		return personService.getPeopleByType(PersonType.STUDENT);
	}

	@GetMapping("/student/{studentId}/profile")
    public ResponseEntity<List<UnitFinalGradeDto>> getStudentProfile(
            @PathVariable Long studentId) {
        
        try {
            List<UnitFinalGradeDto> profile = studentService.getStudentProfile(studentId);
            return ResponseEntity.ok(profile);
        } catch (DEIException e) {
            System.err.println("Error getting student profile: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Unexpected error getting student profile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get student assessment deliveries for calendar view
     * Moved from EnrollmentController to maintain domain organization
     */
    @GetMapping("/student/{studentId}/deliveries")
    public ResponseEntity<List<AssessmentDeliveryDto>> getStudentAssessmentDeliveries(
            @PathVariable Long studentId) {
        
        try {
            List<AssessmentDeliveryDto> deliveries = studentService.getStudentAssessmentDeliveries(studentId);
            return ResponseEntity.ok(deliveries);
        } catch (DEIException e) {
            System.err.println("Error getting student deliveries: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Unexpected error getting student deliveries: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get student progress statistics
     * Moved from EnrollmentController to maintain domain organization
     */
    @GetMapping("/student/{studentId}/progress")
    public ResponseEntity<ProgressDto> getStudentProgress(
            @PathVariable Long studentId) {
        
        try {
            ProgressDto progress = studentService.getStudentProgress(studentId);
            return ResponseEntity.ok(progress);
        } catch (DEIException e) {
            System.err.println("Error getting student progress: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Unexpected error getting student progress: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}