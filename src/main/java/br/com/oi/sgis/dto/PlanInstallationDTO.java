package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.InstallationReasonEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class PlanInstallationDTO {
    @NotBlank(message = "A unidade deve ser informada")
    private String unityId;
    @NotNull(message = "O motivo da instalação deve ser informado")
    private InstallationReasonEnum installationReason;
    @NotBlank(message = "A estação deve ser informada")
    private String stationId;
    @NotBlank(message = "O número BA/BD deve ser informado")
    private String baBdCode;
    @NotBlank(message = "O técnico deve ser informada")
    private String technicianId;
    private String sinisterNumber;
    private String boNumber;
    @ApiModelProperty(hidden = true)
    private String lostReason;

    @JsonIgnore
    private String central;

    public String getLostReason() {
        return installationReason.getLostReason();
    }
}
