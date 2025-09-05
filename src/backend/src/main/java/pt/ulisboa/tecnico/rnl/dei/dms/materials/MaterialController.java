package pt.ulisboa.tecnico.rnl.dei.dms.materials;


import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.rnl.dei.dms.files.File;
import pt.ulisboa.tecnico.rnl.dei.dms.files.FileService;
import pt.ulisboa.tecnico.rnl.dei.dms.materials.dto.CreateMaterialDto;
import pt.ulisboa.tecnico.rnl.dei.dms.materials.dto.MaterialDto;
import pt.ulisboa.tecnico.rnl.dei.dms.materials.service.MaterialService;

import java.io.IOException;
import java.util.List;

@RestController
public class MaterialController {

    private final MaterialService materialService;
    private final FileService fileService;

    public MaterialController(MaterialService materialService, FileService fileService) {
        this.materialService = materialService;
        this.fileService = fileService;
    }

    @GetMapping("/curriculum-units/{unitId}/materials")
    public List<MaterialDto> getMaterials(@PathVariable Long unitId) {
        return materialService.getMaterialsByCurriculumUnit(unitId);
    }

    @PostMapping("/materials")
    public MaterialDto createMaterial(@RequestBody CreateMaterialDto createMaterialDto) {
        return materialService.createMaterial(createMaterialDto);
    }

    @PostMapping(value = "/materials/{materialId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MaterialDto addFileToMaterial(@PathVariable Long materialId,
                                        @RequestPart("file") MultipartFile file) throws IOException {
        return materialService.addFileToMaterial(materialId, file);
    }

    @DeleteMapping("/materials/{materialId}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long materialId) {
        materialService.deleteMaterial(materialId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        materialService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws IOException {
        File file = fileService.getFileById(fileId);
        Resource resource = fileService.loadFileAsResource(file.getPath());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }
}