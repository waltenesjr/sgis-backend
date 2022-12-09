package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ModelTechnicianDTO {
    @NotNull(message = "O contrato não deve ser nulo")
    private DepartmentDTO department;
    private TechnicalStaffDTO technicalStaff;
    private AreaEquipamentDTO model;
    private Long priority;

}
