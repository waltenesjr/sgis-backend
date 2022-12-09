package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder @Data
public class GeneralItensReportDTO {
    private Long totalItens;
    private String typeGroup;
    private String groupBy;
    private List<GeneralItensDTO> generalItens;
}
