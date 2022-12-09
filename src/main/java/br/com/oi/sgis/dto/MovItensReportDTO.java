package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.MovItensReportOrderEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class MovItensReportDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "O período inicial de movimentação deve ser informado")
    private LocalDateTime initialPeriod;
    @NotNull(message = "O período final de movimentação deve ser informado")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endPeriod;
    private String unityCode;
    private String fromResponsible;
    private String toResponsible;
    private String fromTechnician;
    private String toTechnician;
    private List<String> fromSituations;
    private List<String> toSituations;
    private MovItensReportOrderEnum orderBy;
    private boolean breakTotals;

    public MovItensReportOrderEnum getOrderBy(){
        if(this.orderBy==null)
            return MovItensReportOrderEnum.DATA;
        return this.orderBy;
    }
}
