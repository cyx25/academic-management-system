package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto.CreateTesteDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto.StudentTesteDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto.TesteDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.services.TesteService;

import java.io.IOException;
import java.util.List;

@RestController
public class TesteController {

    private final TesteService testeService;

    public TesteController(TesteService testeService) {
        this.testeService = testeService;
    }

    @GetMapping("/curriculum-units/{unitId}/tests")
    public List<TesteDto> getTests(@PathVariable Long unitId) {
        return testeService.getTestsByCurriculumUnit(unitId);
    }

    @PostMapping("/curriculum-units/{unitId}/tests")
    public TesteDto createTest(@PathVariable Long unitId,
                              @RequestBody CreateTesteDto createTesteDto) {
        return testeService.createTeste(unitId, createTesteDto);
    }

    @PostMapping("/tests/{testId}/statement")
    public TesteDto addStatementFile(@PathVariable Long testId,
                                    @RequestParam("file") MultipartFile file) throws IOException {
        return testeService.addStatementFile(testId, file);
    }

    @PostMapping("/tests/{testId}/correction")
    public TesteDto addCorrectionFile(@PathVariable Long testId,
                                     @RequestParam("file") MultipartFile file) throws IOException {
        return testeService.addCorrectionFile(testId, file);
    }

    @GetMapping("/tests/{testId}/student-tests")
    public List<StudentTesteDto> getStudentTests(@PathVariable Long testId) {
        return testeService.getStudentTestesByTeste(testId);
    }

    @PostMapping("/student-tests/{studentTestId}/grade")
    public ResponseEntity<Void> gradeStudentTest(@PathVariable Long studentTestId,
                                               @RequestBody GradeRequest gradeRequest) {
        testeService.gradeStudentTeste(studentTestId, gradeRequest.teacherId(), gradeRequest.grade());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tests/{testId}/students/{studentId}")
    public StudentTesteDto getStudentTest(@PathVariable Long testId, @PathVariable Long studentId) {
        return testeService.getStudentTeste(testId, studentId);
    }

    @GetMapping("/curriculum-units/{unitId}/students/{studentId}/tests")
    public List<StudentTesteDto> getStudentTests(@PathVariable Long unitId, @PathVariable Long studentId) {
        return testeService.getStudentTestsForStudent(unitId, studentId);
    }

    public record GradeRequest(Long teacherId, Float grade) {}
}