package br.com.oi.sgis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter @AllArgsConstructor
public class TicketForwardedDTO {
    private String situationRepair;
    private String situationDescription;
    private String repairCenter;
    private String barcode;
    private String brNumber;
    private LocalDateTime date;
    private String devolution;
    private String origin;
    private String unityCode;
    private String description;
    private String mnemonic;

    @JsonIgnore
    public String getDate(){
        if(date==null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

}
