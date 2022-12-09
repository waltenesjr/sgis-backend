package br.com.oi.sgis.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor @Getter
public class OperatorTicketDTO {
    private String brNumber;
    private LocalDateTime date;
    private String situation;
    private String defect;
    private String defectDescription;
    private String repairCenter;
    private String technician;
    private String technicianName;
    private String company;
    private String companyName;
    private String companyTradeName;

    public String getDate() {
        if(date==null)
            return "";
        return getDateString(date);
    }

    public String getDateString(LocalDateTime date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
}
