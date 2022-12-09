package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SituationSapEnum {

    E("E", "Erro"),
    S("S", "Sucesso");

    private final String cod;
    private final String description;
}
