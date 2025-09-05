package pt.ulisboa.tecnico.rnl.dei.dms.assessments.files;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "assessment_files")
public class AssessmentFile {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Lob
    @Column(name = "data", nullable = false)
    private byte[] data;

    public AssessmentFile(AssessmentFileDto dto) {
        this.fileName = dto.fileName();
        this.contentType = dto.contentType();
        this.data = dto.data();
    }


}
