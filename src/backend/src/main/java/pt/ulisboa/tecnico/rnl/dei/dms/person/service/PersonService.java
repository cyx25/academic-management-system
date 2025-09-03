package pt.ulisboa.tecnico.rnl.dei.dms.person.service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person.PersonType;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

// Service class for managing Person entities
@Service
@Transactional
public class PersonService {

	@Autowired
	private PersonRepository personRepository;

	private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

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
		validatePersonDto(personDto, null);
		return savePersonDto(null, personDto); // id automatic
	}

	@Transactional
	public PersonDto updatePerson(long id, PersonDto personDto) {
		validatePersonDto(personDto, id);
		fetchPersonOrThrow(id); // ensure exists
		return savePersonDto(id, personDto);
	}



	private void validatePersonDto(PersonDto personDto, Long id) {

		if (personDto.name() == null || personDto.name().trim().isEmpty()) {
			throw new DEIException(ErrorMessage.PERSON_NAME_REQUIRED);
		}
		if (personDto.istId() == null || personDto.istId().trim().isEmpty()) {
			throw new DEIException(ErrorMessage.PERSON_IST_ID_REQUIRED);
		}
		if (personDto.type() == null || personDto.type().trim().isEmpty()) {
			throw new DEIException(ErrorMessage.PERSON_TYPE_REQUIRED);
		}	

		if (!personDto.name().matches("^[a-zA-Z]+(\\s[a-zA-Z]+)*$")) {
    		throw new DEIException(ErrorMessage.PERSON_NAME_NOT_VALID);
		}

		int person_istID;
		try {
			person_istID = Integer.parseInt(personDto.istId());
		} catch (NumberFormatException e) {
			throw new DEIException(ErrorMessage.PERSON_IST_ID_NOT_VALID);
		}

	
		if (person_istID < 0) {
			throw new DEIException(ErrorMessage.INVALID_PERSON_ID);
		}

		// Validate email format
		if (personDto.email() != null && !personDto.email().isEmpty()) {
			if (!EMAIL_PATTERN.matcher(personDto.email()).matches()) {
				throw new DEIException(ErrorMessage.INVALID_EMAIL_FORMAT);
			}
		}

		// Validate IST ID uniqueness
		if (personDto.istId() != null && !personDto.istId().isEmpty()) {
			Person existingPerson = personRepository.findByIstId(personDto.istId());
			if (existingPerson != null) {
				// For updates, allow if it's the same person
				if (id == null || !existingPerson.getId().equals(id)) {
					throw new DEIException(ErrorMessage.PERSON_ALREADY_EXISTS, personDto.istId());
				}
			}
		}
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

	@Transactional(readOnly = true)
    public List<PersonDto> getPeopleByType(PersonType type) {
        return personRepository.findByType(type).stream()
                .map(PersonDto::new)
                .collect(Collectors.toList());
    }
}
