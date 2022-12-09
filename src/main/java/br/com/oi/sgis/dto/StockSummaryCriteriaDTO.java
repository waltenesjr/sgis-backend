package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.FilteringEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
public class StockSummaryCriteriaDTO {
    private String modelCode;
    @NotNull(message = "Entre com a área administrativa!")
    private String responsibleCode;
    @NotNull(message = "Filtragem é obrigatória")
    private FilteringEnum filtering;
    private boolean analysis;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime initialPeriod;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finalPeriod;
}
