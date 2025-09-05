package pt.ulisboa.tecnico.rnl.dei.dms.assessments.files;

public record AssessmentFileDto(
    Long id,
    String fileName,
    String contentType,
    byte[] data
) {

    public AssessmentFileDto(AssessmentFile assessmentFile) {
        this(
            assessmentFile.getId(),
            assessmentFile.getFileName(),
            assessmentFile.getContentType(),
            assessmentFile.getData()
        );
    }
    
}
