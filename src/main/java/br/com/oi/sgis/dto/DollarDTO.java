package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.Utils;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder @Data
public class DollarDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "A data deve ser informada")
    private LocalDateTime date;
    @NotNull(message = "O valor deve ser informado")
    private BigDecimal value;

    public LocalDateTime getDate() {
        return Utils.onlyDate(date);
    }
}
