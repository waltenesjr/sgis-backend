package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyModelDTO {

    @NotNull(message = "A empresa não pode ser nula")
    private CompanyDTO company;
    private DepartmentDTO department;
    @NotNull(message = "A unidade deve ser informada")
    private AreaEquipamentDTO equipament;
    private Long priority;
}
