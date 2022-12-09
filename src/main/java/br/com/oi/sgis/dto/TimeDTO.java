package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class TimeDTO {

    @NotNull(message = "A unidade não deve ser nula")
    private AreaEquipamentDTO unityModel;
    @NotNull(message = "A intervenção não deve ser nula")
    private InterventionDTO intervention;
    private BigDecimal centesimalTime;
    @Size(message = "O procedimento não deve possuir mais que 250 caracteres", max = 250)
    private String procedure;
}
