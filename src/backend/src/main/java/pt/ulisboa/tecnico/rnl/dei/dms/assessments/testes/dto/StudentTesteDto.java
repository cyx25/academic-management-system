package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.StudentTeste;

public record StudentTesteDto(
    Long id,
    Long studentId,
    String studentName,
    Long testeId,
    String testeTitle,
    Float grade,
    Long gradedTeacherId,
    String gradedTeacherName,
    boolean hasRevision,
    boolean canRequestRevision,
    boolean isPendingRevision
) {
    public StudentTesteDto(StudentTeste studentTeste) {
        this(
            studentTeste.getId(),
            studentTeste.getStudent().getId(),
            studentTeste.getStudent().getName(),
            studentTeste.getTeste().getId(),
            studentTeste.getTeste().getTitle(),
            studentTeste.getGrade(),
            studentTeste.getGradedTeacher() != null ? studentTeste.getGradedTeacher().getId() : null,
            studentTeste.getGradedTeacher() != null ? studentTeste.getGradedTeacher().getName() : null,
            studentTeste.hasRevision(),
            studentTeste.canRequestRevision(),
            studentTeste.isPendingRevision()
        );
    }
}