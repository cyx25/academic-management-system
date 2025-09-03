package pt.ulisboa.tecnico.rnl.dei.dms.assignments.teachings;

import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;

public record TeachingDto(
    Long id,
    PersonDto teacher
) {
    public TeachingDto(Teaching teaching) {
        this(
            teaching.getId(),
            new PersonDto(teaching.getTeacher())
        );
    }
}