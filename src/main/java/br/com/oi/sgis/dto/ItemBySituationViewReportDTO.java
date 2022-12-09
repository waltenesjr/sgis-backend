package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemBySituationViewReportDTO {
    private String department;
    private List<ItemBySituationViewDTO> analiticList;
    private ItemBySituationViewDTO totalByDepartment;
}
