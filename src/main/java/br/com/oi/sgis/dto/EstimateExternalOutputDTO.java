package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder @Data
public class EstimateExternalOutputDTO {
    @NotBlank(message = "Número de orçamento deve ser informado.")
    private String estimateId;
    @NotNull(message = "Data de nota fiscal deve ser informada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fiscalNoteDate;
    @NotBlank(message = "Número de nota fiscal deve ser informado")
    private String fiscalNote;
}
