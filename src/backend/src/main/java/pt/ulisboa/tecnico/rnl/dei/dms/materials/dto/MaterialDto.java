package pt.ulisboa.tecnico.rnl.dei.dms.materials.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.files.FileDto;
import pt.ulisboa.tecnico.rnl.dei.dms.materials.domain.Material;

import java.util.List;
import java.util.stream.Collectors;

public record MaterialDto(
    Long id,
    String name,
    String iconName,
    List<FileDto> files
) {
    public MaterialDto(Material material) {
        this(
            material.getId(),
            material.getName(),
            material.getIconName(),
            material.getFiles().stream()
                .map(FileDto::new)
                .collect(Collectors.toList())
        );
    }
}