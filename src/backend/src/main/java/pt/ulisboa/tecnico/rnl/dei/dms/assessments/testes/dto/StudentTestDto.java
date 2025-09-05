package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.Teste;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;

public record StudentTestDto (
    Long id,
    TesteDto testeDto,
    PersonDto personDto,
    Float grade

) {

   public StudentTestDto(Long id, Teste teste, Person person, Float grade) {
       this(
       id,
       new TesteDto(teste),
       new PersonDto(person),
       grade);
   }
} 




    

