package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComponentMovTypeDTO {
    @NotBlank(message = "O código de tipo de mov. de componente não deve ser nulo")
    @Size(max = 5,message = "O código de tipo de mov. de componente deve possuir no máximo 5 caracteres")
    private String id;
    @NotBlank(message = "A descrição de tipo de mov. de componente não deve ser nulo")
    @Size(max = 30,message = "A descrição de tipo de mov. de componente deve possuir no máximo 30 caracteres")
    private String description;
    @NotNull(message = "O tipo de movimento não deve ser nulo")
    private MovTypeDTO type;
    @NotNull(message = "O sinal não deve ser nulo")
    private SignalMovTypeDTO signal;
}
