package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoxTypeDTO {
    @NotBlank(message = "O código de tipo de caixa não deve ser nulo")
    @Size(max = 10,message = "O código de tipo de caixa deve possuir no máximo 10 caracteres")
    private String id;
    @NotBlank(message = "A descrição de tipo de caixa não deve ser nulo")
    @Size(max = 60,message = "A descrição de tipo de caixa deve possuir no máximo 60 caracteres")
    private String description;
}
