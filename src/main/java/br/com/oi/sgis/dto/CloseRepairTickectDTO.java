package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data @Builder
public class CloseRepairTickectDTO {
    @NotBlank(message = "Você deve informar um código de barras")
    private String barcode;
    @NotBlank(message = "Você deve informar a nova situação")
    private String finalSituation;
    private String location;
    private String userId;
}
