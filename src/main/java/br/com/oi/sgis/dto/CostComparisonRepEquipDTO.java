package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class CostComparisonRepEquipDTO {
    private String equipmentType;
    private String equipmentTypeDescription;
    private String equipment;
    private String equipmentDescription;
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
    List<CostComparisonRepDTO> unities;

    public Integer getInternal() {
        return unities.stream().mapToInt(CostComparisonRepDTO::getInternal).sum();
    }

    public Integer getExternal() {
        return unities.stream().mapToInt(CostComparisonRepDTO::getExternal).sum();
    }

    public Integer getWarranty() {
        return unities.stream().mapToInt(CostComparisonRepDTO::getWarranty).sum();

    }

    public Integer getWaste() {
        return unities.stream().mapToInt(CostComparisonRepDTO::getWaste).sum();

    }

    public Integer getNoDefect() {
        return unities.stream().mapToInt(CostComparisonRepDTO::getNoDefect).sum();

    }

    public BigDecimal getValueRi() {
        return unities.stream().map(CostComparisonRepDTO::getValueRi).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValueRe() {
        return unities.stream().map(CostComparisonRepDTO::getValueRe).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValueWarranty() {
        return unities.stream().map(CostComparisonRepDTO::getValueWarranty).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTimeRi() {        return
            unities.stream().map(CostComparisonRepDTO::getTimeRi).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTimeRe() {        return
            unities.stream().map(CostComparisonRepDTO::getTimeRe).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTimeWarranty() {
        return unities.stream().map(CostComparisonRepDTO::getTimeWarranty).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getInternalTotal() {
        return unities.stream().map(CostComparisonRepDTO::getInternalTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getExternalTotal() {
        return unities.stream().map(CostComparisonRepDTO::getExternalTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getWarrantyTotal() {
        return unities.stream().map(CostComparisonRepDTO::getWarrantyTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValueUnityRi() {
        return unities.stream().map(CostComparisonRepDTO::getValueUnityRi).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValueUnityRe() {
        return unities.stream().map(CostComparisonRepDTO::getValueUnityRe).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }
}
