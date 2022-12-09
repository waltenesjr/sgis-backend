package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder @Data
public class ComponentTypeDTO {
    @NotBlank(message = "O código de tipo de componente não deve ser nulo")
    @Size(max = 5,message = "O código de tipo de componente deve possuir no máximo 5 caracteres")
    private String id;
    @NotBlank(message = "A descrição de tipo de componente não deve ser nulo")
    @Size(max = 35,message = "A descrição de tipo de componente deve possuir no máximo 35 caracteres")
    private String description;

}