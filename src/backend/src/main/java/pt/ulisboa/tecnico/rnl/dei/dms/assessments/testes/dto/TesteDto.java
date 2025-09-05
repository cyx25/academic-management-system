package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.dto;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.Teste;


public record TesteDto(
    Long id, 
    String title, 
    Float weight, 
    String statementPath, 
    String correctionPath,
    LocalDateTime date

    ) {

        public TesteDto(Teste test) {
            this(
                test.getId(),
                test.getTitle(),
                test.getWeight(),
                test.getStatementPath(),
                test.getCorrectionPath(),
                test.getDate()
            );
        }

}


