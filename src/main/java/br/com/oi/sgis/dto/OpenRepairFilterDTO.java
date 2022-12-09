package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
@Data
public class OpenRepairFilterDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime initialDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finalDate;
    private String repairCenter;
    private String origin;
    private String devolution;
    private boolean notForwarded;
    private boolean externalRepair;
    private boolean internalRepair;

}
