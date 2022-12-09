package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ComponentMovDTO {

    private String component;
    private String componentDescription;
    private Long sequence;
    private Long quantity;
    private Integer departmentQuantity;
    private String departmentId;
    private TechnicalStaffDTO technician;
    private ComponentMovTypeDTO componentMovType;
    private LocalDateTime date;
    private Long previousBalance;
    private String description;
    private String document;
    private BigDecimal value;
    private BigDecimal movCost;
    private BigDecimal previousCost;
    private String brNumber;
    private String sequenceIntervention;

}
