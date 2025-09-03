package pt.ulisboa.tecnico.rnl.dei.dms.assignments.teachings;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

@Data
@NoArgsConstructor
@Entity
@Table(name = "teachings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"curriculum_unit_id"}) // Only one main teacher per CU
})
public class Teaching {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "curriculum_unit_id", nullable = false, updatable = false)
    private CurriculumUnit curriculumUnit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Person teacher;

    public Teaching(CurriculumUnit curriculumUnit, Person teacher) {
        this.curriculumUnit = curriculumUnit;
        this.teacher = teacher;
    }
}