package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SapReprocessConditionEnum {
    FIRST(1, "Condição 1"),
    SECOND(2, "Condição 2"),
    THIRD(3, "Condição 3"),
    NONE(0, "Nenhuma condição foi atendida");


    private final Integer condition;
    private final String description;
}
