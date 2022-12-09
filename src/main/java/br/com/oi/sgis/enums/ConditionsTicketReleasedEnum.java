package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConditionsTicketReleasedEnum {
    DVR("DVR", "Liberado para devolução"),
    TDR("TDR", "Em trânsito de devolução");


    private final String condition;
    private final String description;
}
