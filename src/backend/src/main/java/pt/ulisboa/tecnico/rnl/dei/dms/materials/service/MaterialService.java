package pt.ulisboa.tecnico.rnl.dei.dms.materials.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.repository.CurriculumUnitRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.files.File;
import pt.ulisboa.tecnico.rnl.dei.dms.files.FileService;
import pt.ulisboa.tecnico.rnl.dei.dms.materials.domain.Material;
import pt.ulisboa.tecnico.rnl.dei.dms.materials.dto.CreateMaterialDto;
import pt.ulisboa.tecnico.rnl.dei.dms.materials.dto.MaterialDto;
import pt.ulisboa.tecnico.rnl.dei.dms.materials.repository.MaterialRepository;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CurriculumUnitRepository curriculumUnitRepository;
    private final FileService fileService;

    public MaterialService(MaterialRepository materialRepository,
                          CurriculumUnitRepository curriculumUnitRepository,
                          FileService fileService) {
                       
        this.materialRepository = materialRepository;
        this.curriculumUnitRepository = curriculumUnitRepository;
        this.fileService = fileService;
    }

    @Transactional(readOnly = true)
    public List<MaterialDto> getMaterialsByCurriculumUnit(Long curriculumUnitId) {
        List<Material> materials = materialRepository.findByCurriculumUnitId(curriculumUnitId);
        return materials.stream()
                .map(MaterialDto::new)
                .collect(Collectors.toList());
    }

    public MaterialDto createMaterial(CreateMaterialDto createMaterialDto) {
        CurriculumUnit curriculumUnit = curriculumUnitRepository.findById(createMaterialDto.curriculumUnitId())
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_CU));

                
        Material material = new Material(
                createMaterialDto.name(),
                createMaterialDto.iconName(),
                curriculumUnit
        );

        Material savedMaterial = materialRepository.save(material);
        return new MaterialDto(savedMaterial);
    }

    public MaterialDto addFileToMaterial(Long materialId, MultipartFile file) throws IOException {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_MATERIAL));

        File savedFile = fileService.storeFile(file);
        material.addFile(savedFile);
        
        Material updatedMaterial = materialRepository.save(material);
        return new MaterialDto(updatedMaterial);
    }

    public void deleteFile(Long fileId) {
        File file = fileService.getFileById(fileId);
        Material material = file.getMaterial();
        
        if (material != null) {
            material.removeFile(file);
            materialRepository.save(material);
        }
        
        fileService.deleteFile(file.getPath());
    }

    public void deleteMaterial(Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_MATERIAL));

        // Delete all associated files
        material.getFiles().forEach(file -> {
            try {
                fileService.deleteFile(file.getPath());
            } catch (Exception e) {
                // Log error but continue deletion
                System.err.println("Error deleting file: " + e.getMessage());
            }
        });

        materialRepository.delete(material);
    }
}