package pt.ulisboa.tecnico.rnl.dei.dms.person.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

// Service class for managing Person entities
@Service
@Transactional
public class PersonService {

	@Autowired
	private PersonRepository personRepository;

	private Person fetchPersonOrThrow(long id) {
		return personRepository.findById(id)
				.orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(id)));
	}

	@Transactional
	public List<PersonDto> getPeople() {
		return personRepository.findAll().stream()
				.map(PersonDto::new)
				.toList();
	}
	@Transactional
	public PersonDto getPerson(long id) {
		return new PersonDto(fetchPersonOrThrow(id));
	}

	@Transactional
	public PersonDto createPerson(PersonDto personDto) {
		return savePersonDto(null, personDto); // id automatic
	}

	@Transactional
	public PersonDto updatePerson(long id, PersonDto personDto) {
		fetchPersonOrThrow(id); // ensure exists
		return savePersonDto(id, personDto);
	}

	private PersonDto savePersonDto(Long id, PersonDto personDto) {
		Person person = new Person(personDto);
		person.setId(id); // null for create, actual id for update
		return new PersonDto(personRepository.save(person));
	}

	@Transactional
	public void deletePerson(long id) {
		fetchPersonOrThrow(id); // ensure exists

		personRepository.deleteById(id);
	}
}
