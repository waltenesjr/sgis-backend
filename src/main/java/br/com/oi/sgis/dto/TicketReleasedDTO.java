package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter @AllArgsConstructor
public class TicketReleasedDTO {
    private String brNumber;
    private LocalDateTime date;
    private String barcode;
    private String unityCode;
    private String unityDescription;
    private String mnemonic;
    private String devolution;
    private String origin;
    private LocalDateTime closeDate;
    private LocalDateTime devolutionDate;
    private BigDecimal value;

    public String getDate() {
        return getDateString(date);
    }

    private String getDateString(LocalDateTime dateToString) {
        if (dateToString == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateToString.format(formatter);
    }

    public String getCloseDate() {
        return getDateString(closeDate);
    }

    public String getDevolutionDate() {
        return getDateString(devolutionDate);
    }

    public BigDecimal getValue() {
        if(value==null)
            return BigDecimal.ZERO;
        return value;
    }
}
