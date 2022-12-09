package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder @Data
public class TicketForwardedReportDTO {
    private List<TicketForwardedDTO> items;
    private String situation;
    private String situationDescription;
    private int totalItems;
}
