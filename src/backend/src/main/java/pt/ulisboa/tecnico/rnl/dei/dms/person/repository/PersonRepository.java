package pt.ulisboa.tecnico.rnl.dei.dms.person.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person.PersonType;

// Repository interface for managing Person entities
@Repository
@Transactional
public interface PersonRepository extends JpaRepository<Person, Long> {

    Person findByIstId(String istId);
    List<Person> findByType(PersonType type);
}
