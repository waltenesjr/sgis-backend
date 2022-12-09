package br.com.oi.sgis.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class PhysicalElectricalPropsDTO {
    private String id;
    private String description;
    private BigDecimal measureUnity;
    private BigDecimal value;
    private String measureId;
    private BigDecimal measureEquipment;
    private String barcode;
    private String unityCode;
    private String responsible;
    private String modelEquipment;
    private String equipmentType;


    public BigDecimal getMeasureUnity() {
        return measureUnity.setScale(2,RoundingMode.HALF_UP);
    }

    public PhysicalElectricalPropsDTO(String id, String description, BigDecimal measureUnity, BigDecimal value, String measureId, BigDecimal measureEquipment, String barcode, String unityCode,
                                      String responsible, String modelEquipment, String equipmentType) {
        this.id = id;
        this.description = description;
        this.measureUnity = measureUnity;
        this.value = value;
        this.measureId = measureId;
        this.measureEquipment = measureEquipment;
        this.barcode = barcode;
        this.unityCode = unityCode;
        this.responsible = responsible;
        this.modelEquipment = modelEquipment;
        this.equipmentType = equipmentType;
    }


    public BigDecimal getInitialTrack() {
        return BigDecimal.ZERO;
    }

    public BigDecimal getFinalTrack() {
        return BigDecimal.valueOf(9999999);
    }

    public BigDecimal getMeasureEquipment() {
        if(measureId == null || measureId.isEmpty())
            return null;
        return measureEquipment;
    }

    public String getMeasureId() {
        if(measureId!= null && measureId.isEmpty())
            return null;
        return measureId;
    }
}
