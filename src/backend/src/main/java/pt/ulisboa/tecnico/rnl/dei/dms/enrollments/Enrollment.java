package pt.ulisboa.tecnico.rnl.dei.dms.enrollments;

import jakarta.persistence.*;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

@Data
@Entity
@Table(name = "enrollments")
public class Enrollment { // this will have projects and evaluations later

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Person student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "curriculum_unit_id", nullable = false)
    private CurriculumUnit curriculumUnit;

    @Column(name = "grade", nullable = false)
    private double grade;


    public Enrollment(Person student, CurriculumUnit curriculumUnit, double grade) {
        this.student = student;
        this.curriculumUnit = curriculumUnit;
        this.grade = grade;
    }

}