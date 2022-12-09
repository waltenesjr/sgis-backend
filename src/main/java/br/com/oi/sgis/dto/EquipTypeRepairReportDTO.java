package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class EquipTypeRepairReportDTO {
    private List<EquipamentTypeRepairDTO> items;
    private String repairCenter;

}
