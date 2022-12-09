package br.com.oi.sgis.dto;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CostComparisonRepDTO {
    private String repairCenter;
    private String equipment;
    private String description;
    private String equipmentModel;
    private String equipmentModelDescription;
    private String equipmentType;
    private String equipmentTypeDescription;
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
    private String id;
    public void setId(String id) {
        this.id = id;
    }

    @Id
    public String getId() {
        return id;
    }
}
