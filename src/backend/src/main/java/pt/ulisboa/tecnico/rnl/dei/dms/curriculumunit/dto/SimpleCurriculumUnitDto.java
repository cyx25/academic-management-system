package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;

public record SimpleCurriculumUnitDto(
    Long id,
    String name,
    String code,
    String mainTeacher
) {
    public SimpleCurriculumUnitDto(CurriculumUnit curriculumUnit) {
        this(
            curriculumUnit.getId(),
            curriculumUnit.getName(),
            curriculumUnit.getCode(),
            curriculumUnit.getMainTeacher() != null ?
            curriculumUnit.getMainTeacher().getName() : null
        );
    }
}