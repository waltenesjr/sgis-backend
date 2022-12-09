package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class OpenRepairDTO {
    private String brNumber;
    private LocalDateTime openDate;
    private LocalDateTime prevision;
    private String barcode;
    private LocalDateTime acceptDate;
    private String situation;
    private String externalRepair;
    private String estimate;
    private LocalDateTime sendDate;
    private String companyName;
    private String repairCenter;
    private String equipmentId;
    private String mnemonic;
    private String descriptionEquipment;
    private String intervention;
    private String interventionDescription;
    private String observation;

    public String getIntervention() {
        if(intervention == null)
            return "";
        return intervention;
    }

    public String getInterventionDescription() {
        if(interventionDescription == null)
            return "";
        return interventionDescription;
    }

    public String getOpenDate() {
        return getDateString(openDate);
    }

    public String getPrevision() {
        return getDateString(prevision);
    }

    public String getAcceptDate() {
        return getDateString(acceptDate);
    }

    public String getSendDate() {
        return getDateString(sendDate);
    }

    public String getDescription() {
        return equipmentId + " - " + (mnemonic!=null? mnemonic + " - " + descriptionEquipment : descriptionEquipment);
    }

    private String getDateString(LocalDateTime dateToString) {
        if (dateToString == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateToString.format(formatter);
    }
}
