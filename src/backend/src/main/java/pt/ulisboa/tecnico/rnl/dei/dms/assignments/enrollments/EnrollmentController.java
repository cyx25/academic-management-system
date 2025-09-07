package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.AssessmentDeliveryDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.FinalGradeBreakdownDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.ProgressDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.dto.UnitFinalGradeDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.services.EnrollmentService;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;

@RestController
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /**
     * Get final grade breakdown for a student in a specific curriculum unit
     * Shows all assignments (tests/projects) with their weights and grades
     */
    @GetMapping("/final-grade-breakdown")
    public ResponseEntity<FinalGradeBreakdownDto> getFinalGradeBreakdown(
            @RequestParam("studentId") Long studentId,
            @RequestParam("curriculumUnitId") Long curriculumUnitId) {

        FinalGradeBreakdownDto breakdown = enrollmentService.getFinalGradeBreakdown(studentId, curriculumUnitId);
        return ResponseEntity.ok(breakdown);
    }

    
}