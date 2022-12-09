package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.Utils;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class ReportDollarCrudDTO {
    private BigDecimal value;
    private List<String> sortAsc;
    private List<String> sortDesc;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;

    public List<String> getSortAsc() {
        if(sortAsc == null)
            return List.of();
        return sortAsc;
    }

    public List<String> getSortDesc() {
        if(sortDesc == null)
            return List.of();
        return sortDesc;
    }
    public LocalDateTime getDate() {
        return Utils.onlyDate(date);
    }

}
