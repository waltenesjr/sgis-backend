package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder @Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralItensDTO {
    private String unityId;
    private String situationId;
    private LocalDateTime situationDate;
    private String depositId;
    private String responsibleId;
    private String location;
    private String unityCodeId;
    private String description;
    private String modelId;
    private String modelDescription;
    private String serieNumber;
    private String technicianId;
    private String technicianName;
    private String stationId;
    private String mnemonic;
    private String companyName;
    private String technicianPhone;
    private Boolean equipamentDescont;
    private Boolean unityDescont;

    public String getEquipamentDescont(){
        return getBool(this.equipamentDescont);
    }

    public String getUnityDescont(){
        return getBool(this.unityDescont);
    }

    private String getBool(Boolean bool) {
        String descont = null;
        if (Boolean.TRUE.equals(bool)) {
            descont = "Sim";
        } else if (Boolean.FALSE.equals(bool))
            descont = "Não";

        return descont;
    }

    public String getSituationDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if(situationDate != null)
            return situationDate.format(formatter);
        return null;
    }

}
