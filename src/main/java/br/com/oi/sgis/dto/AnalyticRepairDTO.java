package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor @Getter
public class AnalyticRepairDTO {
    private String brNumber;
    private String barcode;
    private LocalDateTime date;
    private String unityCode;
    private String description;
    @Setter
    private String repairExternalIn;
    private String origin;
    private String repairCenter;
    private String defect;
    private String defectDescription;
    private String situation;
    private BigDecimal value;
    private LocalDateTime warranty;
    private String warrantyProvider;
    private String modelEquipment;

    public BigDecimal getValue() {
        if(value==null)
            return BigDecimal.ZERO;
        return value;
    }

    public String getWarranty() {
        return getDateString(warranty);
    }

    private String getDateString(LocalDateTime dateToString) {
        if (dateToString == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateToString.format(formatter);
    }

    public String getDate() {
        return getDateString(date);
    }

    public String getRepairExternalIn() {
        if(repairExternalIn==null)
            return "NENHUM";
        return repairExternalIn;
    }
}
