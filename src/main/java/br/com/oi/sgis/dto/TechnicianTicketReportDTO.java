package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TechnicianTicketReportDTO {
    private List<TechnicianTicketDTO> technicianTicket;
    private String technicianId;
    private String technicianName;
}
