package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class ApproveCancelItemEstimateDTO {
    private String estimateID;
    private Long sequence;
    private String brNumber;
    private Boolean approve;
}
