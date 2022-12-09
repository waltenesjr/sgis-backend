package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItensSumaryReportBreakEnum {

    EQUIPAMENTO("EQUIPAMENTO", "Equipamento", "b.equipModelCode.id"),
    UNIDADE("UNIDADE", "Unidade", "b.id"),
    ESTACAO("ESTACAO", "Estação", "a.station.id");

    private final String orderBy;
    private final String description;
    private final String attribute;
}
