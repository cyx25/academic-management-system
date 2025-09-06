package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.domain;

import jakarta.persistence.*;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;


@Data
@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "curriculum_unit_id"})
})
public class Enrollment {

    public enum EnrollmentStatus {
        ENROLLED,
        APPROVED,
        FAILED
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Integer finalGrade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Person student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "curriculum_unit_id", nullable = false)
    private CurriculumUnit curriculumUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    // this will then have projects, etc


    protected Enrollment() {}

    public Enrollment(Person student, CurriculumUnit curriculumUnit) {
        this.student = student;
        this.curriculumUnit = curriculumUnit;
        this.status = EnrollmentStatus.ENROLLED;
    }


}