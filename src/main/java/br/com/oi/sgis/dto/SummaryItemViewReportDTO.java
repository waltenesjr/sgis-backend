package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SummaryItemViewReportDTO {
    private List<SummaryItemViewDTO> groupItens;
    private Long totalItens;
    private String typeGroup;
    private String groupBy;
}
