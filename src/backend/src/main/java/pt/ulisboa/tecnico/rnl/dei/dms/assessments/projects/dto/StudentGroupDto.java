package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.StudentGroup;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;

import java.util.List;
import java.util.stream.Collectors;

public record StudentGroupDto(
    Long id,
    Integer groupId,
    Float grade,
    List<PersonDto> members,
    List<SubmissionDto> submissions,
    String gradedTeacherName
) {
    public StudentGroupDto(StudentGroup studentGroup, List<SubmissionDto> submissions) {
        this(
            studentGroup.getId(),
            studentGroup.getGroupID(),
            studentGroup.getGrade(),
            studentGroup.getStudents().stream()
                .map(PersonDto::new)
                .collect(Collectors.toList()),
            submissions,
            studentGroup.getResponsibleGradedTeacher() != null ? 
                studentGroup.getResponsibleGradedTeacher().getName() : "POR CORRIGIR"
        );
    }
}