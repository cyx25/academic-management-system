package pt.ulisboa.tecnico.rnl.dei.dms.files;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import pt.ulisboa.tecnico.rnl.dei.dms.materials.domain.Material;

@Data
@Entity
@Table(name = "files")
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private Long size; // Size in KB

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    @JsonIgnore
    private Material material;

    public File(String fileName, String contentType, String path, Long size, LocalDateTime uploadDate) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.path = path;
        this.size = size;
        this.uploadDate = uploadDate;
    }

   

}