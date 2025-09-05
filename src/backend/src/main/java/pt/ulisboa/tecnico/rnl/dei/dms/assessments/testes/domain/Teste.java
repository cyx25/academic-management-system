package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto.TesteDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "testes")
@NoArgsConstructor
public class Teste {


    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_unit_id", nullable = false)
    private CurriculumUnit curriculumUnit;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Float weight;

    @Column(name = "statement_path") // o aluno nao ve este
    private String statementPath;

    @Column(name = "test_date", nullable = false)
    private LocalDateTime date;

    @Column(name = "correction_path")
    private String correctionPath;

    public Teste(TesteDto testeDto, CurriculumUnit curriculumUnit){
        this.id = testeDto.id();
        this.title = testeDto.title();
        this.weight = testeDto.weight();
        this.statementPath = testeDto.statementPath();
        this.date = testeDto.date();
        this.correctionPath = testeDto.correctionPath();
        this.curriculumUnit = curriculumUnit;
    }
}