package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain;



import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.domain.Revision;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;


@Data
@Entity
@Table(name = "student_testes")
@NoArgsConstructor
public class StudentTeste {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teste_id", nullable = false)
    private Teste teste;


   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Person student;

    @Column
    private Float grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_teacher_id")
    private Person gradedTeacher;

    @OneToOne(mappedBy = "studentTeste", cascade = CascadeType.ALL)
    private Revision revision;

    // TODO: implement revisoes

    public StudentTeste(Teste teste, Person student) {
        this.teste = teste;
        this.student = student;
    }

    public boolean hasRevision() {
        return revision != null;
    }

    public boolean canRequestRevision() {
        return grade != null && !hasRevision();
    }

    public boolean isPendingRevision() {
        return hasRevision() && revision.isPending();
    }

}

