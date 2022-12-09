package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.math.RoundingMode.HALF_UP;

@Data
@Builder
public class ProductivityComparisonReportDTO {

    private List<ProductivityComparisonDTO> items;
    private String technicalStaffCod;
    private String technicalStaffName;
    private BigDecimal spentTimeTotal;
    private BigDecimal spentTimeAverageTotal;
    private BigDecimal averageIntTimeTotal;
    private BigDecimal repairTimeTotal;
    private BigDecimal averageExtTimeTotal;
    private BigDecimal repairValueTotal;
    private BigDecimal averageIntValueTotal;
    private BigDecimal averageExtValueTotal;

    public Integer getTotalItens(){
        return items.size();
    }

    public BigDecimal getSpentTimeTotal() {
        return items.stream().map(ProductivityComparisonDTO::getSpentTime).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

    }

    public BigDecimal getSpentTimeAverageTotal() {
        return getSpentTimeTotal()
                .divide(BigDecimal.valueOf(getTotalItens()), 2, HALF_UP);

    }

    public BigDecimal getRepairTimeTotal() {
        return items.stream().map(x -> Optional.of(x.getRepairTime()).orElse(BigDecimal.ZERO)).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getAverageRepairTimeTotal() {
        return getRepairTimeTotal()
                    .divide(BigDecimal.valueOf(getTotalItens()), 2, HALF_UP);

    }

    public BigDecimal getAverageExtTimeTotal() {
        return items.stream().map(ProductivityComparisonDTO::getAverageExtTime).reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(getTotalItens()), 2, HALF_UP);
    }

    public BigDecimal getRepairValueTotal() {
        return items.stream().map(ProductivityComparisonDTO::getRepairValue).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getRepairValueAverage(){
       return getRepairValueTotal().divide(BigDecimal.valueOf(getTotalItens()), 2, HALF_UP);
    }

    public BigDecimal getAverageIntValueTotal() {
        return items.stream().map(ProductivityComparisonDTO::getAverageIntValue).reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(getTotalItens()), 2, HALF_UP);
    }

    public BigDecimal getAverageExtValueTotal() {
        return items.stream().map(ProductivityComparisonDTO::getAverageExtValue).reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
                              .divide(BigDecimal.valueOf(getTotalItens()), 2, HALF_UP);
    }


}
