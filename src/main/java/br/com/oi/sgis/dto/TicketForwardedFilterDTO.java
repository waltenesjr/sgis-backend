package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.ConditionsTicketForwardedEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
@Data
public class TicketForwardedFilterDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime initialDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finalDate;
    private String repairCenter;
    private String origin;
    private String devolution;
    private ConditionsTicketForwardedEnum condition;

}
