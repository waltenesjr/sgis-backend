package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SignalMovTypeEnum {

    P("P", "Positivo" ),
    N("N", "Negativo");

    private final String id;
    private final String description;
}
