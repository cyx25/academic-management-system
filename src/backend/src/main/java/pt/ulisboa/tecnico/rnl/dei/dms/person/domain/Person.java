package pt.ulisboa.tecnico.rnl.dei.dms.person.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.Assist;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.Enrollment;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;

// Domain class representing a person in the system
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"assists", "enrollments"})
@EqualsAndHashCode(exclude = {"assists", "enrollments"})
@Entity
@Table(name = "people")
public class Person {

	public enum PersonType {
		ADMINISTRATOR,
		MAIN_TEACHER,
		TEACHING_ASSISTANT,
		STUDENT
		// maybe add more types later?
	}

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "ist_id", nullable = false, unique = true)
	private String istId;

	@Column(name = "email", nullable = true)
	private String email;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
    private PersonType type;

	@OneToMany(mappedBy = "mainTeacher", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<CurriculumUnit> curriculumUnits = new HashSet<>();

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments = new HashSet<>();

    @OneToMany(mappedBy = "assistant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Assist> assists = new HashSet<>();



	public Person(String name, String istId, String email, PersonType type) {
		this.name = name;
		this.istId = istId;
		this.email = email;
		this.type = type;
	}

	public Person(PersonDto personDto) {
		this(personDto.name(), personDto.istId(), personDto.email(),
				PersonType.valueOf(personDto.type().toUpperCase()));
		System.out.println("PersonDto: " + personDto);
		System.out.println("PersonType: " + personDto.type());

	}


}
