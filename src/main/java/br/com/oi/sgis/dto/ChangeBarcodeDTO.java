package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data @Builder
public class ChangeBarcodeDTO {
    @NotBlank(message = "O código de barras deve ser informado")
    private String oldbarcode;
    @NotBlank(message = "O novo código de barras deve ser informado")
    private String newBarcode;
}
