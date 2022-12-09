package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder @Data
public class UnitExtractionReportDTO {
    private String application;
    private String applicationDesc;
    private String barcode;
    private String box;
    private String composition;
    private LocalDateTime registerDate;
    private LocalDateTime warrantyDate;
    private LocalDateTime situationDate;
    private String depositary;
    private String depositaryDesc;
    private String destiny;
    private Boolean discontinuedFlag;
    private String station;
    private String manufacturerEquip;
    private String manufacturerEquipDesc;
    private String manufacturerUnit;
    private String manufacturerUnitDesc;
    private String installationGroup;
    private String installationRack;
    private String installationClient;
    private String installationClientDesc;
    private String stationInst;
    private String location;
    private String mnemonic;
    private String equipmentModel;
    private String equipmentModelDesc;
    private String unityCode;
    private String unityDescription;
    private Integer provider;
    private String serieNumber;
    private String baNumber;
    private String observation;
    private String providerResponsible;
    private String providerResponsibleDesc;
    private String responsible;
    private String responsibleDescription;
    private String situation;
    private String situationDescription;
    private String technic;
    private String technician;
    private String technicianName;
    private String installationTechnician;
    private String installationTechnicianDesc;
    private String technology;
    private String boxType;
    private String boxTypeDesc;
    private String equipmentType;
    private String equipmentTypeDesc;
    private Boolean discontinuedFlagEquip;
    private Integer providerAccountant;
    private String uf;
    private BigDecimal value;
    private BigDecimal areaValue;
    private Integer quantity;


    public String getSituationDate() {
        return getDate(situationDate);
    }

    public String getDiscontinuedFlag() {
        return getBoolean(discontinuedFlag);
    }

    public String getDiscontinuedFlagEquip() {
        return getBoolean(discontinuedFlagEquip);
    }

    public String getRegisterDate() {
        return getDate(registerDate);
    }

    public String getWarrantyDate() {
        return getDate(warrantyDate);
    }

    private String getDate(LocalDateTime date){
        if(date ==null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    private String getBoolean(boolean data){
        return data ? "Sim" : "Não";
    }

}
