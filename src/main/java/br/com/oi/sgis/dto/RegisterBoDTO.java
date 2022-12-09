package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class RegisterBoDTO {
    @NotBlank(message = "A unidade deve ser informada")
    private String unityId;
    @NotBlank(message = "O número de BO deve ser informado")
    @Size(max = 30,message = "O número do BO deve possuir no máximo 20 caracteres")
    private String boNumber;
}
