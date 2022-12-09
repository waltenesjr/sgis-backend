package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CostComparisonReportBreakEnum {
    EQUIPAMENTO("EQUIPAMENTO", "Equipamento" ),
    UNIDADE("UNIDADE", "Unidade"),
    TIPO("TIPO", "Tipo" );

    private final String orderBy;
    private final String description;
}
