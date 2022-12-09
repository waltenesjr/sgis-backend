package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class CostComparisonRepReportDTO {
    private String equipmentType;
    private String description;
    private Integer internal;
    private Integer external;
    private Integer warranty;
    private Integer waste;
    private Integer noDefect;
    private BigDecimal valueRi;
    private BigDecimal valueRe;
    private BigDecimal valueWarranty;
    private BigDecimal timeRi;
    private BigDecimal timeRe;
    private BigDecimal timeWarranty;
    private BigDecimal internalTotal;
    private BigDecimal externalTotal;
    private BigDecimal warrantyTotal;
    private BigDecimal valueUnityRi;
    private BigDecimal valueUnityRe;
    List<CostComparisonRepEquipDTO> listEquipments;

    public Integer getInternal() {
        return listEquipments.stream().mapToInt(CostComparisonRepEquipDTO::getInternal).sum();
    }

    public Integer getExternal() {
        return listEquipments.stream().mapToInt(CostComparisonRepEquipDTO::getExternal).sum();
    }

    public Integer getWarranty() {
        return listEquipments.stream().mapToInt(CostComparisonRepEquipDTO::getWarranty).sum();

    }

    public Integer getWaste() {
        return listEquipments.stream().mapToInt(CostComparisonRepEquipDTO::getWaste).sum();

    }

    public Integer getNoDefect() {
        return listEquipments.stream().mapToInt(CostComparisonRepEquipDTO::getNoDefect).sum();

    }

    public BigDecimal getValueRi() {
        return listEquipments.stream().map(CostComparisonRepEquipDTO::getValueRi).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValueRe() {
        return listEquipments.stream().map(CostComparisonRepEquipDTO::getValueRe).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValueWarranty() {
        return listEquipments.stream().map(CostComparisonRepEquipDTO::getValueWarranty).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTimeRi() {        return
            listEquipments.stream().map(CostComparisonRepEquipDTO::getTimeRi).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTimeRe() {        return
            listEquipments.stream().map(CostComparisonRepEquipDTO::getTimeRe).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTimeWarranty() {
        return listEquipments.stream().map(CostComparisonRepEquipDTO::getTimeWarranty).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getInternalTotal() {
        return listEquipments.stream().map(CostComparisonRepEquipDTO::getInternalTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getExternalTotal() {
        return listEquipments.stream().map(CostComparisonRepEquipDTO::getExternalTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getWarrantyTotal() {
        return listEquipments.stream().map(CostComparisonRepEquipDTO::getWarrantyTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValueUnityRi() {
        return listEquipments.stream().map(CostComparisonRepEquipDTO::getValueUnityRi).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValueUnityRe() {
        return listEquipments.stream().map(CostComparisonRepEquipDTO::getValueUnityRe).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }
}
