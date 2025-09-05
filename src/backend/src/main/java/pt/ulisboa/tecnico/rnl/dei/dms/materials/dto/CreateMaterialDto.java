package pt.ulisboa.tecnico.rnl.dei.dms.materials.dto;



public record CreateMaterialDto(
    
    String name, 
    String iconName,
    Long curriculumUnitId
    
) {}