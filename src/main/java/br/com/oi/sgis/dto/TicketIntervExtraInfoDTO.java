package br.com.oi.sgis.dto;

import br.com.oi.sgis.entity.TicketInterventionID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder @Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketIntervExtraInfoDTO {

    @JsonIgnore
    private TicketInterventionID ticketInterventionID;

    private Boolean externalRepair;
    private String repSituation;
    private LocalDateTime initialDate;
    private LocalDateTime finalDate;
    private String interventionCode;
    private String interventionDescription;
    private String technicianCode;
    private String technicianName;
    private String unity;
    private String unityDescription;
    private String estimate;
    private String company;
    private BigDecimal cpValue;
    private BigDecimal laborValue;
}
