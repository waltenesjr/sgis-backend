package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder @Data
public class DiagnosisDTO {

    @NotBlank(message = "O código do diagnóstico não deve ser nulo")
    @Size(max = 10,message = "O código do diagnóstico deve possuir no máximo 10 caracteres")
    private String id;

    @NotBlank(message = "A descrição do diagnóstico não deve ser nula")
    @Size(max = 60,message = "A descrição do diagnóstico deve possuir no máximo 60 caracteres")
    private String description;
}
