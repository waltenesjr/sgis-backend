package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder @Data
public class DevolutionRepairTicketDTO {

    @NotBlank(message = "Você deve informar um código de barras")
    private String barcode;
    @NotBlank(message = "Você deve informar uma área de devolução")
    private String devolutionArea;
    private String fiscalTelemar;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime devolutionNFdate;
    private String technicianObs;
    private String userId;
}
