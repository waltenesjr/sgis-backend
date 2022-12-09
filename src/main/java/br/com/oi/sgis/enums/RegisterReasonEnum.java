package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegisterReasonEnum {

    CUS("CUS", "Nova Unidade de Sobressalente"),
    CRP("CRP", "Retirado da Planta"),
    CTR("CTR", "Transferencia entre Regioes");

    private final String reason;
    private final String description;
}
