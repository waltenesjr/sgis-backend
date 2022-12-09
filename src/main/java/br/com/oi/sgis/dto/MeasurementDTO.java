package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder @Data
public class MeasurementDTO {

    @NotBlank(message = "O código da unidade de médida não deve ser nulo")
    @Size(max = 10,message = "O código da unidade de médida deve possuir no máximo 10 caracteres")
    private String id;
    @NotBlank(message = "A descrição da unidade de médida não deve ser nula")
    @Size(max = 30,message = "A descrição da unidade de médida deve possuir no máximo 30 caracteres")
    private String description;

}
