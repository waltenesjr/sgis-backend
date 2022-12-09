package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GeneralItensReportBreakEnum {

    EQUIPAMENTO("EQUIPAMENTO", "Equipamento", "me.id"),
    UNIDADE("UNIDADE", "Unidade", "ea.id"),
    RESPONSAVEL("RESPONSAVEL", "Responsável", "un.responsible.id"),
    DEPOSITORIO("DEPOSITORIO", "Depositário", "un.deposit.id"),
    TECNICO("TECNICO", "Técnico", "pt.id"),
    SITUACAO("SITUACAO", "Situação", "un.situationCode.id"),
    ESTACAO("ESTACAO", "Estação", "un.station.id"),
    FABRICANTE("FABRICANTE", "Fabricante", "epr.id");

    private final String orderBy;
    private final String description;
    private final String attribute;
}
