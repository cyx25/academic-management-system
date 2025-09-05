package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;


@Data
@Entity
@Table(name = "student_projects")
@NoArgsConstructor
public class StudentGroup {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;

   
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_group_students",
            joinColumns = @JoinColumn(name = "project_group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Person> students = new HashSet<>();

    @Column(name = "group_id", nullable = false)
    private Integer groupID;

    @Column
    private Float grade;

    @OneToMany(mappedBy = "studentGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("submissionDate DESC")
    @JsonManagedReference
    private List<Submission> submissions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_graded_teacher_id")
    private Person responsibleGradedTeacher;

    public StudentGroup(Project project, int groupID) {

        this.grade = null;
        this.submissions = new ArrayList<>();
        this.responsibleGradedTeacher = null;
        this.project = project;
        this.groupID = groupID;
    }

    
}
