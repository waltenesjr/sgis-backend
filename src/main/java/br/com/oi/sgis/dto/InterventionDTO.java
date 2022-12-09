package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder @Data
public class InterventionDTO {
    @NotBlank(message = "O código de intervenção não deve ser nulo ou em branco. ")
    @Size(max = 10,message = "O código de intervenção deve possuir no máximo 10 caracteres")
    private String id;
    @NotBlank(message = "A descrição de intervenção não deve ser nulo ou em branco. ")
    @Size(max = 60,message = "A descrição de intervenção deve possuir no máximo 60 caracteres")
    private String description;
    private Boolean internalFlag;
    private Boolean externalFlag;
    private Boolean productiveFlag;
    private Boolean calibrationFlag;

}
