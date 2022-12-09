package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder @AllArgsConstructor @Data
public class PackingLabelParametersDTO {
    private Long labelQuantity;
    @Min(1) @Max(3) @NotNull(message = "Deve ser informada uma quantidade")
    private Integer labelByLine;
    private Long topMargin;
    private Long lateralMargin;
    private Long labelHeight;
    private Long labelWidth;
    private Long verticalRange;
    private Long horizontalRange;
    private Boolean inhibitBarcode;
    private List<String> barcodes;
}
