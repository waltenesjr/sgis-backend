package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DepartmentComponentDTO {

    private ComponentDTO component;

    private DepartmentDTO department;

    private BigDecimal value;

    private Integer quantity;

    private String location;
}
