package pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.domain.Assist;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;

public record AssistDto(
    Long id,
    PersonDto assistant
) {
    public AssistDto(Assist assist) {
        this(
            assist.getId(),
            new PersonDto(assist.getAssistant())
        );
    }
}