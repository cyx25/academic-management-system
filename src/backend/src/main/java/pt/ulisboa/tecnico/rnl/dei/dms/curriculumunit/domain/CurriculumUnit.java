package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.Assist;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.Enrollment;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.teachings.Teaching;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.domain.Course;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.CurriculumUnitDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;


@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"courses", "teaching", "assists", "enrollments"})
@EqualsAndHashCode(exclude = {"courses", "teaching", "assists", "enrollments"})
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
    private String semester;

    @Column(name = "ects", nullable = false)
    private String ects;


    // course
    @ManyToMany
    @JoinTable(
        name = "curriculum_unit_courses",
        joinColumns = @JoinColumn(name = "curriculum_unit_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )

    @JsonManagedReference
    private Set<Course> courses = new HashSet<>();

    @OneToOne(mappedBy = "curriculumUnit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Teaching teaching;

    @OneToMany(mappedBy = "curriculumUnit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Assist> assists = new HashSet<>();

    @OneToMany(mappedBy = "curriculumUnit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments = new HashSet<>();



    public void setTeaching(Person teacher) {
        if (teacher != null) {
            this.teaching = new Teaching(this, teacher);
        } else {
            this.teaching = null;
        }
    }

    public void addAssistant(Person assistant) {
        Assist assist = new Assist(this, assistant);
        this.assists.add(assist);
    }

    public void addStudent(Person student) {
        Enrollment enrollment = new Enrollment(student, this);
        this.enrollments.add(enrollment);
    }


    public CurriculumUnit(String name, String code, String semester, String ects) {
        this.name = name;
        this.code = code;
        this.semester = semester;
        this.ects = ects;
    }

    public CurriculumUnit(CurriculumUnitDto curriculumUnitDto, Person mainTeacher, Set<Course> courses) {
        this(
            curriculumUnitDto.name(),
            curriculumUnitDto.code(),
            curriculumUnitDto.semester(),
            curriculumUnitDto.ects()
        );
        setTeaching(mainTeacher);
        this.courses = courses != null ? courses : new HashSet<>();

        // debug in console, print everything
        System.out.println("=== Creating CurriculumUnit ===");
        System.out.println("Main Teacher: " + (mainTeacher != null ? mainTeacher.getName() : "None"));
        System.out.println("Courses Count: " + (courses != null ? courses.size() : 0));
        if (courses != null && !courses.isEmpty()) {
            System.out.println("Course Codes: " + 
                courses.stream()
                    .map(Course::getCode)
                    .collect(Collectors.joining(", ")));
        }
        System.out.println("===============================");

    }

    // TODO implement additions
}