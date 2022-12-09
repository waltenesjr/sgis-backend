package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.CostComparisonReportBreakEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder
@Data
public class CostComparisonRepairFilterDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime initialDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finalDate;
    @NotBlank(message = "Deve ser informado o centro de reparo")
    private String repairCenter;
    @NotBlank(message = "Deve ser informado um detalhe")
    private CostComparisonReportBreakEnum detail;
}
