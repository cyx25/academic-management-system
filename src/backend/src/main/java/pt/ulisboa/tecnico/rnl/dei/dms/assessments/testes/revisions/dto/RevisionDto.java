package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.domain.Revision;

import java.time.LocalDateTime;

public record RevisionDto(
    Long id,
    Long studentTesteId,
    String studentName,
    String testeTitle,
    Float currentGrade,
    String justification,
    String status,
    LocalDateTime requestDate,
    String reviewedBy,
    LocalDateTime reviewDate,
    Float newGrade
) {
    public RevisionDto(Revision revision) {
        this(
            revision.getId(),
            revision.getStudentTeste().getId(),
            revision.getStudentTeste().getStudent().getName(),
            revision.getStudentTeste().getTeste().getTitle(),
            revision.getStudentTeste().getGrade(),
            revision.getJustification(),
            revision.getStatus().toString(),
            revision.getRequestDate(),
            revision.getReviewedBy() != null ? revision.getReviewedBy().getName() : null,
            revision.getReviewDate(),
            revision.getNewGrade()
        );
    }
}