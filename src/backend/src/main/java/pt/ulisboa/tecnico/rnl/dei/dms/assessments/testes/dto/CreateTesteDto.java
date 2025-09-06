package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto;


import java.time.LocalDateTime;

public record CreateTesteDto(
   
    String title,
    Float weight,
    LocalDateTime date,
    String duration
    
) {}