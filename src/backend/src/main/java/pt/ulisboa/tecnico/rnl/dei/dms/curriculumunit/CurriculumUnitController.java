package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit;

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

import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.dto.AssistDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.services.AssistService;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.EnrollmentDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.services.EnrollmentService;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.CurriculumUnitDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.CurriculumUnitStatisticsDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.service.CurriculumUnitService;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;

@RestController
public class CurriculumUnitController {


    @Autowired
	private CurriculumUnitService curriculumUnitService;

	@Autowired
    private EnrollmentService enrollmentService;

	@Autowired
    private AssistService assistService;

	@GetMapping("/curriculum-units")
	public List<CurriculumUnitDto> getCurriculumUnits() {
		return curriculumUnitService.getCurriculumUnits();
	}

	@PostMapping("/curriculum-units")
	public CurriculumUnitDto createCurriculumUnit(@RequestBody CurriculumUnitDto curriculumUnitDto) {
		return curriculumUnitService.createCurriculumUnit(curriculumUnitDto);
	}

	@GetMapping("/curriculum-units/{id}")
	public CurriculumUnitDto getCurriculumUnit(@PathVariable long id) {
		return curriculumUnitService.getCurriculumUnit(id);
	}

	@PutMapping("/curriculum-units/{id}")
	public CurriculumUnitDto updateCurriculumUnit(@PathVariable long id, @RequestBody CurriculumUnitDto curriculumUnitDto) {
		return curriculumUnitService.updateCurriculumUnit(id, curriculumUnitDto);
	}

	@DeleteMapping("/curriculum-units/{id}")
	public void deleteCurriculumUnit(@PathVariable long id) {
		curriculumUnitService.deleteCurriculumUnit(id);
	}

	// --- Student Enrollment Endpoints ---

    @PostMapping("/curriculum-units/{id}/students")
    public EnrollmentDto addStudent(@PathVariable long id, @RequestBody PersonDto student) {
        return enrollmentService.createEnrollment(id, student.id());
    }

    @DeleteMapping("/curriculum-units/{cuId}/students/{studentId}")
    public void removeStudent(@PathVariable long cuId, @PathVariable long studentId) {
        enrollmentService.deleteEnrollment(cuId, studentId);

    }

	@GetMapping("/curriculum-units/{id}/students")
    public List<EnrollmentDto> getStudents(@PathVariable long id) {
        return enrollmentService.getEnrollments(id);
    }

    // --- Teaching Assistant Endpoints ---

    @PostMapping("/curriculum-units/{id}/assistants")
    public AssistDto addAssistant(@PathVariable long id, @RequestBody PersonDto assistant) {
        return assistService.createAssist(id, assistant.id());
    }

    @DeleteMapping("/curriculum-units/{cuId}/assistants/{assistantId}")
    public void removeAssistant(@PathVariable long cuId, @PathVariable long assistantId) {
        assistService.deleteAssist(cuId, assistantId);
    }

    @GetMapping("/curriculum-units/{id}/assistants")
    public List<AssistDto> getAssistants(@PathVariable long id) {
        return assistService.getAssists(id);
    }

    @GetMapping("/{curriculumUnitId}/statistics")
    public ResponseEntity<CurriculumUnitStatisticsDto> getCurriculumUnitStatistics(
            @PathVariable Long curriculumUnitId) {
        
        try {
            CurriculumUnitStatisticsDto statistics = curriculumUnitService.getCurriculumUnitStatistics(curriculumUnitId);
            return ResponseEntity.ok(statistics);
        } catch (DEIException e) {
            System.err.println("Error getting curriculum unit statistics: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Unexpected error getting curriculum unit statistics: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


}
