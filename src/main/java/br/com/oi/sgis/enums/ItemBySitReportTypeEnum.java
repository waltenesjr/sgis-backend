package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemBySitReportTypeEnum {

    ANALITICO("ANALITICO", "Analítico"),
    SINTETICO("SINTETICO", "Sintetico");

    private final String cod;
    private final String description;
}
