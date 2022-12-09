package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.InstallationReasonEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor @Data
public class ItensInstallByStealReasonReportDTO {
    private String reasonInstallation;
    private List<ItensInstallByStealReasonDTO> itens;

    public String getReasonInstallation() {

        if (InstallationReasonEnum.IFM.getCod().equals(reasonInstallation)) {
            return "Itens Instalados na Planta por Motivo de Furto com Reposição por Mesmo Modelo";
        }
        return "Itens Instalados na Planta por Motivo de Furto com Reposição por Modelo Diferente";
    }
}
