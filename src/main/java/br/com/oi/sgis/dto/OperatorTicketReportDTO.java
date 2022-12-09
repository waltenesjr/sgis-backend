package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder @Data
public class OperatorTicketReportDTO {
    private List<OperatorTicketDTO> items;
    private String company;
    private String companyName;
    private int totalItems;
}
