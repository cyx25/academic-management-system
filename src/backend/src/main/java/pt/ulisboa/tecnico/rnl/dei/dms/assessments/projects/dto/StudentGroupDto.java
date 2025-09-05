package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto;

import java.util.List;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.StudentGroup;


public record StudentGroupDto(
    Long id,
    Float grade,
    Float tempGrade,
    String gradedTeacherName,
    Integer groupID,
    List<SubmissionDto> submissions

) {

    public StudentGroupDto(StudentGroup studentGroup, List<SubmissionDto> submissions) {
        this(
            studentGroup.getId(),
            studentGroup.getGrade(),
            studentGroup.getTempGrade(),
            studentGroup.getResponsibleGradedTeacher().getName(),
            studentGroup.getGroupID(),
            submissions
        );
    }


    
}
