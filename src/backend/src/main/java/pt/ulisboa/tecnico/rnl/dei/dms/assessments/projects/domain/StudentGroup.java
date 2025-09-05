package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain;



import java.util.HashSet;
import java.util.Set;

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
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.dto.StudentGroupDto;
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

    @Column
    private Float tempGrade;



    @OneToMany(mappedBy = "studentGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Submission> submissions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_graded_teacher_id")
    private Person responsibleGradedTeacher;

    public StudentGroup(StudentGroupDto studentGroupDto, Project project, Set<Person> students) {
        
        this.grade = studentGroupDto.grade();
        this.tempGrade = studentGroupDto.tempGrade();
        this.submissions = new HashSet<>();
        this.responsibleGradedTeacher = null;
        this.project = project;
        this.students = students;
        this.groupID = studentGroupDto.groupID();
    }

    
}
