package pt.ulisboa.tecnico.rnl.dei.dms.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.ProjectRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private final FileRepository assessmentFileRepository;

    public FileService(FileRepository assessmentFileRepository) {
        this.assessmentFileRepository = assessmentFileRepository;
    }


    public File storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new DEIException(ErrorMessage.EMPTY_FILE);
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFileName = timestamp + "_" + UUID.randomUUID().toString() + fileExtension;
        
        // Store file
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create and save AssessmentFile entity
        File assessmentFile = new File(
            originalFileName,
            file.getContentType(),
            filePath.toString(),
            file.getSize(),
            LocalDateTime.now()
        );
        
        return assessmentFileRepository.save(assessmentFile);
    }

    public Resource loadFileAsResource(String filePath) throws IOException {
        try {
            Path path = Paths.get(filePath).normalize();
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new DEIException(ErrorMessage.NO_SUCH_FILE);
            }
        } catch (Exception ex) {
            throw new DEIException(ErrorMessage.NO_SUCH_FILE);
        }
    }

    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            System.err.println("Could not delete file: " + filePath + ". Error: " + ex.getMessage());
        }
    }

    public File getFileById(Long fileId) {
        return assessmentFileRepository.findById(fileId)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_FILE));
    }
}