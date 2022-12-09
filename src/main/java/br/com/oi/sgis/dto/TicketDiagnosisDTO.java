package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDiagnosisDTO {
    private String diagnosisId;
    private String diagnosis;
    private String position;
    private Long sequence;
    private String brNumber;

}
