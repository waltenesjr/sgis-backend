package br.com.oi.sgis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Data
public class RegisteredItensCriteriaDTO {
    private String number;
    private String responsibleId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime initialPeriod;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finalPeriod;
    private boolean filterByPeriod;
    private boolean filterByNumber;

    @JsonIgnore
    public String getPeriod() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if(initialPeriod != null)
            return initialPeriod.format(formatter) + " - " + finalPeriod.format(formatter);
        return "-";
    }
}
