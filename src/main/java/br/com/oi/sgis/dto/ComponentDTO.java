package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Builder @Data
public class ComponentDTO {
    @NotBlank(message = "O código do componente não deve ser nulo")
    @Size(max = 30,message = "O código do componente deve possuir no máximo 30 caracteres")
    private String id;
    @NotNull(message = "O tipo do componente deve ser informado")
    private ComponentTypeDTO componentType;
    @NotBlank(message = "A descrição do componente não deve ser nula")
    @Size(max = 30,message = "A descrição do componente deve possuir no máximo 30 caracteres")
    private String description;
    @NotNull(message = "O valor do componente não deve ser nulo")
    @Digits(integer = 7, fraction = 2, message = "O valor deve possuir o formato 9999999.99, com no máximo 7 casas inteiras e 2 casas decimais")
    private BigDecimal value;
}
