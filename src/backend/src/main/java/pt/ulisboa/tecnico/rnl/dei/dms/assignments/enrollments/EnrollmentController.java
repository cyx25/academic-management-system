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

    @GetMapping("/student/{studentId}/profile")
    public ResponseEntity<List<UnitFinalGradeDto>> getStudentProfile(
            @PathVariable Long studentId) {
        
        try {
            List<UnitFinalGradeDto> profile = enrollmentService.getStudentProfile(studentId);
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
     */
    @GetMapping("/student/{studentId}/deliveries")
    public ResponseEntity<List<AssessmentDeliveryDto>> getStudentAssessmentDeliveries(
            @PathVariable Long studentId) {
        
        try {
            List<AssessmentDeliveryDto> deliveries = enrollmentService.getStudentAssessmentDeliveries(studentId);
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
     */
    @GetMapping("/student/{studentId}/progress")
    public ResponseEntity<ProgressDto> getStudentProgress(
            @PathVariable Long studentId) {
        
        try {
            ProgressDto progress = enrollmentService.getStudentProgress(studentId);
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