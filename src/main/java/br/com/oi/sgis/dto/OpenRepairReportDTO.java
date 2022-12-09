package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class OpenRepairReportDTO {
    private List<OpenRepairDTO> items;
    private String repairCenter;
}
