package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.Teste;
import pt.ulisboa.tecnico.rnl.dei.dms.files.FileDto;


public record TesteDto(
    Long id, 
    String title, 
    Float weight, 
    LocalDateTime date,
    FileDto statementFile,
    FileDto correctionFile,
    String duration

    ) {

        public TesteDto(Teste test) {
            this(
                test.getId(),
                test.getTitle(),
                test.getWeight(),
                test.getDate(),
                test.getStatementFile() != null ? new FileDto(test.getStatementFile()) : null,
                test.getCorrectionFile() != null ? new FileDto(test.getCorrectionFile()) : null,
                test.getDuration()

            );
        }

}


