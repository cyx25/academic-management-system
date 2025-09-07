package pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

@Data
@NoArgsConstructor
@Entity
@Table(name = "assists", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"curriculum_unit_id", "assistant_id"})
})
public class Assist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "curriculum_unit_id", nullable = false)
    private CurriculumUnit curriculumUnit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assistant_id", nullable = false)
    private Person assistant;

    public Assist(CurriculumUnit curriculumUnit, Person assistant) {
        this.curriculumUnit = curriculumUnit;
        this.assistant = assistant;
    }
}