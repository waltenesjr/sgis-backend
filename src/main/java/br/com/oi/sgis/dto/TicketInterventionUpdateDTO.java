package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class TicketInterventionUpdateDTO {
    private TicketInterventionDTO ticketIntervention;
    private  Boolean forwardRepair;
    private SituationDTO situationDTO;
}
