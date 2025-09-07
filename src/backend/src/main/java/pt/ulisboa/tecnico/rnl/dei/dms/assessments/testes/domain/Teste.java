package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto.CreateTesteDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.files.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statement_file_id") // o aluno nao ve este
    private File statementFile;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String duration;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "correction_file_id")
    private File correctionFile;

    @OneToMany(mappedBy = "teste", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentTeste> studentTestes = new HashSet<>();

    public Teste(CreateTesteDto createTesteDto, CurriculumUnit curriculumUnit) {
        this.title = createTesteDto.title();
        this.weight = createTesteDto.weight();
        this.date = createTesteDto.date();
        this.duration = createTesteDto.duration();
        this.curriculumUnit = curriculumUnit;
    }

    public void setStatementFile(File statementFile) {
        this.statementFile = statementFile;
    }

    public void setCorrectionFile(File correctionFile) {
        this.correctionFile = correctionFile;
    }
}