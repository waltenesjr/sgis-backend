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
public class ProductivityComparisonDTO {

    private String technicalStaffCod;
    private String technicalStaffName;
    private String unity;
    private String unityDescription;
    private String brNumber;
    private BigDecimal spentTime;
    private BigDecimal averageIntTime;
    private BigDecimal repairTime;
    private BigDecimal averageExtTime;
    private BigDecimal repairValue;
    private BigDecimal averageIntValue;
    private BigDecimal averageExtValue;
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    @Id
    public String getId() {
        return id;
    }

    private BigDecimal getBigDecimalValue(BigDecimal value){
        if(value == null)
            return BigDecimal.ZERO;
        return value;
    }

    public BigDecimal getSpentTime() {
        return getBigDecimalValue(spentTime);
    }

    public BigDecimal getAverageIntTime() {
        return getBigDecimalValue(averageIntTime);
    }

    public BigDecimal getRepairTime() {
        return getBigDecimalValue(repairTime);
    }

    public BigDecimal getAverageExtTime() {
        return getBigDecimalValue(averageExtTime);
    }

    public BigDecimal getRepairValue() {
        return getBigDecimalValue(repairValue);
    }

    public BigDecimal getAverageIntValue() {
        return getBigDecimalValue(averageIntValue);
    }

    public BigDecimal getAverageExtValue() {
        return getBigDecimalValue(averageExtValue);
    }
}
