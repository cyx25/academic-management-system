package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.StudentTeste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto.StudentTestDto;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "revisions")
@NoArgsConstructor
public class Revision {

    public enum Status {
        REQUESTED,
        UNDER_REVIEW,
        APPROVED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_test_id", nullable = false)
    private StudentTeste studentTeste;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.REQUESTED;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;
}