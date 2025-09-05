package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain;

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
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto.ProjectDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.files.File;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "projects")
@NoArgsConstructor
public class Project {

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "statement_file_id", nullable = false)
    private File statementFile;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "max_group_size", nullable = false)
    private Integer maxGroupSize;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentGroup> studentGroups = new HashSet<>();

    public Project(ProjectDto projectDto, CurriculumUnit curriculumUnit, File statementFile) {
        this.title = projectDto.title();
        this.weight = projectDto.weight();
        this.dueDate = projectDto.dueDate();
        this.maxGroupSize = projectDto.maxGroupSize();
        this.curriculumUnit = curriculumUnit;
        this.statementFile = statementFile;
    }
}