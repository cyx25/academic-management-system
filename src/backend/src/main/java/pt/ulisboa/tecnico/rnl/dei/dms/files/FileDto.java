package pt.ulisboa.tecnico.rnl.dei.dms.files;

import java.time.LocalDateTime;

public record FileDto(
    Long id,
    String fileName,
    String contentType,
    Long size,
    LocalDateTime uploadDate
) {

    public FileDto(File assessmentFile) {
        this(
            assessmentFile.getId(),
            assessmentFile.getFileName(),
            assessmentFile.getContentType(),
            assessmentFile.getSize(),
            assessmentFile.getUploadDate()
        );
    }

    public Long getFileSizeInKB() {
        return size != null ? size / 1024L : 0L;
    }
    
}
