package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder @Data
public class RepairExternalReturnDTO {
    @NotBlank(message = "O código de barras deve ser informado")
    private String barcode;
    @NotNull(message = "A data de chegada deve ser informada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime arrivalDate;
    @NotNull(message = "A data da nota fiscal deve ser informada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fiscalNoteDate;
    @NotBlank(message = "A nota fiscal deve ser informada")
    private String fiscalNote;
    private Boolean substitution;
    private String newBarcode;
}
