package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MovTypeEnum {

    E("E", "Entrada" ),
    S("S", "Saída");

    private final String id;
    private final String description;
}
