package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class RecoverItemDTO {
    @NotBlank(message = "A unidade deve ser informada")
    private String unityId;
    private String stationId;
    private String location;
}
