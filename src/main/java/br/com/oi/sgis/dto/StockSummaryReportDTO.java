package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class StockSummaryReportDTO {
    private String unity;
    private String location;
    private Integer dis;
    private Integer reposition;
    private Integer off;
    private Long min;
    private Long repos;
    private Long max;
    private Integer entrance;
    private Integer exit;

}
