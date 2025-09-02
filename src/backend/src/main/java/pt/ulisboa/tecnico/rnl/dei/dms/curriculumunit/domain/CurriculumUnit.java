package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.domain.Course;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.CurriculumUnitDto;
import pt.ulisboa.tecnico.rnl.dei.dms.enrollments.Enrollment;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;



@Data
@Entity
@Table(name = "curriculum_units")
public class CurriculumUnit {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "semester", nullable = false)
    private int semester;

    @Column(name = "ects", nullable = false)
    private int ects;


    
    @ManyToMany
    @JoinTable(
        name = "curriculum_unit_tas",
        joinColumns = @JoinColumn(name = "curriculum_unit_id"),
        inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private Set<Person> teachingAssistants = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "main_teacher_id", nullable = false)
    private Person mainTeacher;

    // course
    @ManyToMany(mappedBy = "curriculumUnits")
    private Set<Course> courses = new HashSet<>();

    @OneToMany(mappedBy = "curriculumUnit", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> studentCurriculumUnits = new HashSet<>();

    protected CurriculumUnit() {
    }

    public CurriculumUnit(String name, String code, int semester, int ects) {
        this.name = name;
        this.code = code;
        this.semester = semester;
        this.ects = ects;
    }

    public CurriculumUnit(CurriculumUnitDto curriculumUnitDto, Person mainTeacher, Collection<Course> courses) {
        this(
            curriculumUnitDto.name(),
            curriculumUnitDto.code(),
            curriculumUnitDto.semester(),
            curriculumUnitDto.ects()
        );
        this.mainTeacher = mainTeacher;
        this.courses = new HashSet<>(courses);
    }

    // TODO implement additions
}