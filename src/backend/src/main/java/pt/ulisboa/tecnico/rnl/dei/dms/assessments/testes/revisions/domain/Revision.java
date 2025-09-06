package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.StudentTeste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.dto.CreateRevisionDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "revisions")
@NoArgsConstructor
public class Revision {

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
        TA_GRADED

    }

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_test_id", nullable = false, unique = true)
    private StudentTeste studentTeste;

    @Column(nullable = false, length = 500)
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private Person reviewedBy;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "new_grade")
    private Float newGrade;

    public Revision(CreateRevisionDto createRevisionDto, StudentTeste studentTeste) {
        this.studentTeste = studentTeste;
        this.justification = createRevisionDto.justification();
        this.requestDate = LocalDateTime.now();
        this.status = Status.PENDING;
    }

    public boolean isPending() {
        return Revision.Status.PENDING.equals(status);
    }

    public boolean isApproved() {
        return Revision.Status.APPROVED.equals(status);
    }

    public boolean isRejected() {
        return Revision.Status.REJECTED.equals(status);
    }
}