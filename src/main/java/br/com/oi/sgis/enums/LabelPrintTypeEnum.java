package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LabelPrintTypeEnum {
    LINHA("LINHA", "Cod. barras duplicado por linha"),
    DUPLICADO("DUPLICADO", "Cod. barras duplicado em duas linhas");

    private final String cod;
    private final String description;
}
