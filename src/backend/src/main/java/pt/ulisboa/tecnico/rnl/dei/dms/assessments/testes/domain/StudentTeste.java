package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @JoinColumn(name = "student_id")
    private Person student;

    @Column
    private Float grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_graded_teacher_id")
    private Person responsibleGradedTeacher;

    // TODO: implement revisoes

    public StudentTeste(Teste teste, Person student) {
        this.teste = teste;
        this.student = student;
    }


}