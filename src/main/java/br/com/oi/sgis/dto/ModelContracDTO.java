package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data @Builder
public class ModelContracDTO {
    @NotBlank(message = "O contrato não deve ser nulo")
    private String contract;
    @NotBlank(message = "O modelo não deve ser nulo")
    private String model;
    private BigDecimal value;
}
