package br.com.oi.sgis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CableMovementDTO {

    private Long sequence;
    private UnityDTO unity;
    private LocalDateTime date;
    private ElectricalPropDTO electricalProperty;
    private ComponentMovTypeDTO componentMovType;
    private Long quantity;
    private BigDecimal value;
    private TechnicalStaffDTO technician;
    private BigDecimal previousBalance;
    private BigDecimal previousCost;
    private String document;
    private DepartmentDTO department;
    private String job;
    private BigDecimal cost;
    private String balance;

    @JsonIgnore
    public String getDateString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    public BigDecimal getCost(){
        if(quantity==null)
            return null;
        if(componentMovType.getType().getId().equals("E") && componentMovType.getSignal().getId().equals("P"))
            return (previousCost.multiply(previousBalance)).add(value
                    .multiply(BigDecimal.valueOf(quantity))).divide(BigDecimal.valueOf(quantity).add(previousBalance));
        return previousCost;
    }

    public BigDecimal getBalance(){
        if(quantity==null)
            return null;
        if((componentMovType.getType().getId().equals("E") && componentMovType.getSignal().getId().equals("P")) ||
                (componentMovType.getType().getId().equals("S") && componentMovType.getSignal().getId().equals("N")))
            return previousBalance.add(BigDecimal.valueOf(quantity));
        return previousBalance.subtract(BigDecimal.valueOf(quantity));
    }
}
