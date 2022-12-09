package br.com.oi.sgis.dto;

import br.com.oi.sgis.entity.TicketInterventionID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketInterventionDTO {

    @JsonIgnore
    private TicketInterventionID ticketInterventionID;
    @JsonIgnore
    private String companyEstimate;

    private Boolean externalRepair;
    private SituationDTO repSituation;
    private LocalDateTime initialDate;
    private LocalDateTime finalDate;
    @NotNull(message = "Informe uma intervenção")
    private InterventionDTO intervention;
    @NotNull(message = "Informe um técnico")
    private TechnicalStaffDTO technician;
    private TechnicalStaffDTO operator;
    @NotNull(message = "Informe um bilhete de reparo")
    private RepairTicketDTO repairTicket;
    private String observation;
    @NotNull(message = "Informe uma unidade")
    private UnityDTO unity;
    private Long sequence;
    private String itemEstimate;
    private BigDecimal cpValue;
    private BigDecimal laborValue;
    private List<ComponentMovDTO> ticketComponents;
    private List<TicketDiagnosisDTO> ticketDiagnosis;



}
