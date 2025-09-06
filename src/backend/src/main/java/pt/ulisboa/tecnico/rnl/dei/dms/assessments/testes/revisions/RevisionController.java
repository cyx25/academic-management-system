package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions;

import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.dto.CreateRevisionDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.dto.RevisionDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.services.RevisionService;

import java.util.List;

@RestController
public class RevisionController {

    private final RevisionService revisionService;

    public RevisionController(RevisionService revisionService) {
        this.revisionService = revisionService;
    }

    @PostMapping("/revisions")
    public RevisionDto requestRevision(@RequestBody  CreateRevisionDto createRevisionDto) {
        return revisionService.requestRevision(
                createRevisionDto.studentTesteId(), 
                createRevisionDto.justification()
        );
    }

    @GetMapping("/curriculum-units/{unitId}/revisions/pending")
    public List<RevisionDto> getPendingRevisions(@PathVariable Long unitId) {
        return revisionService.getPendingRevisions(unitId);
    }

    @GetMapping("/curriculum-units/{unitId}/revisions")
    public List<RevisionDto> getAllRevisions(@PathVariable Long unitId) {
        return revisionService.getAllRevisionsByUnit(unitId);
    }

    @PostMapping("/revisions/{revisionId}/grade")
    public RevisionDto gradeRevision(@PathVariable Long revisionId,
                                   @RequestBody GradeRevisionRequest request) {
        return revisionService.gradeRevision(
                revisionId, 
                request.teachingAssistantId(), 
                request.newGrade()
        );
    }

    @PostMapping("/revisions/{revisionId}/process")
    public RevisionDto processRevision(@PathVariable Long revisionId,
                                     @RequestBody ProcessRevisionRequest request) {
        return revisionService.processRevision(
                revisionId, 
                request.action(), 
                request.mainTeacherId()
        );
    }

    @GetMapping("/student-tests/{studentTestId}/revisions")
    public RevisionDto getTestRevisions(@PathVariable Long studentTestId) {
        return revisionService.getRevisionByStudentTeste(studentTestId);
    }

    // Request DTOs for validation
    public record GradeRevisionRequest(
            Long teachingAssistantId, 
            Float newGrade
    ) {}

    public record ProcessRevisionRequest(
            String action, 
            Long mainTeacherId
    ) {}
}