package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElectricalPropUnityDTO {
    private String property;
    private String unity;
    private BigDecimal measure;
    private BigDecimal value;
}
