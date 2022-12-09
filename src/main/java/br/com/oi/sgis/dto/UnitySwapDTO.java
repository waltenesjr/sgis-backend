package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class UnitySwapDTO {
    @NotBlank(message = "A unidade deve ser informada")
    private String unityId;
    @NotBlank(message = "O novo código de barras deve ser informado")
    @Size(max = 40,message = "O código de barras deve possuir no máximo 40 caracteres")
    private String unityNewBarcode;
    @NotBlank(message = "O novo número de séries deve ser informado")
    @Size(max = 30,message = "O número de série deve possuir no máximo 30 caracteres")
    private String newSerieNumber;
    @NotBlank(message = "O novo modelo de unidade deve ser informado")
    private String newUnityCode;

}
