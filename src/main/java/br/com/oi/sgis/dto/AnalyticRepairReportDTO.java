package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Builder @Getter
public class AnalyticRepairReportDTO {
    private List<AnalyticRepairDTO> items;
    private String groupBy;
    private BigDecimal valueTotal;
    private long total;

}
