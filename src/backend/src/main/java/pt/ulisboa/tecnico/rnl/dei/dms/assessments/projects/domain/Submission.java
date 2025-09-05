package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.files.File;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

@Data
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "submission_file_id")
    private File submissionFile;

    @Column
    private float grade;

    @ManyToOne
    @JoinColumn(name = "student_group_id", nullable = false)
    @JsonBackReference
    private StudentGroup studentGroup;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Person student;

    @Column
    private LocalDateTime submissionDate;


    public Submission() {
        this.submissionDate = LocalDateTime.now();
    }
    
    public Submission(StudentGroup studentGroup, Person student,  File submissionFile) {
        this();
        this.studentGroup = studentGroup;
        this.submissionFile = submissionFile;
        this.student = student;
        this.grade = (float) (Math.random() * 20);
    }
}
