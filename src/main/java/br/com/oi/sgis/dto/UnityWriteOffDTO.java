package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UnityWriteOffDTO {
    @NotBlank(message = "A unidade deve ser informada")
    private String unityId;
    @NotNull(message = "O motivo da instalação deve ser informado")
    private ReasonForWriteOffEnum reasonForWriteOff;
    private String technicalReport;
    @NotBlank(message = "A situação deve ser informada")
    private String situationID;
    @ApiModelProperty(hidden = true)
    private String reason;

    public String getReason() {
        return reasonForWriteOff.getReason();
    }


}
