package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder @Data
public class RepairSummaryReportDTO {
    private String repairCenter;
    private List<RepairSummaryQuantityDTO> quantityItems;
    private List<RepairSummaryValueDTO> valueItems;
}
