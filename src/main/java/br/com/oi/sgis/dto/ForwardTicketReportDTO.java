package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class ForwardTicketReportDTO {
    private String unityId;
    private String modelUnity;
    private String situation;
    private String contract;
    private String maintainer;
    private String technician;
}
