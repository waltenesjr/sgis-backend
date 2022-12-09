package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.SituationEnum;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UnitySituationDTO {

    @NotBlank(message = "A unidade deve ser informada")
    private String unityId;
    @NotNull(message = "A nova situação deve ser informada")
    private SituationEnum situation;
    private String reservationId;
    private String stationId;
    private String location;
    private String idUser;
}
