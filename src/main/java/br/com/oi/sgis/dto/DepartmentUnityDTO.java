package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Data
public class DepartmentUnityDTO {
    @NotNull(message = "O código da área adminsitrativa não deve ser nula")
    private DepartmentDTO department;

    @NotNull(message = "O código do modelo de unidade não deve ser nulo")
    private AreaEquipamentDTO modelUnity;

    private Long minStock;
    private Long repositionStock;
    private Long maxStock;
    private StationDTO station;
    @Size(max = 20,message = "A localização deve possuir no máximo 20 caracteres")
    private String location;
    private boolean inactive;
}
