package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemEstimateDTO {
    private TicketInterventionDTO ticketIntervention;
    private String estimate;
    private BigDecimal value;
    private LocalDateTime previsionDate;
    private LocalDateTime returnDate;
    private String returnDoc;
    private LocalDateTime returnDocDate;
    private String provider;
    private LocalDateTime approvalDate;
    private String situation;
    private LocalDateTime cancelDate;
    private boolean analyzed;
    private BigDecimal percentage;
}
