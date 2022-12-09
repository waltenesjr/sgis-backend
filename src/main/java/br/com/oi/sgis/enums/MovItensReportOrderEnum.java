package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MovItensReportOrderEnum {


    DATA("b.DATA", "Data"),
    PARA_COD_PLACA("PARA_COD_PLACA", "Modelo unidade"),
    DE_SIGLA_PROP("DE_SIGLA_PROP", "Área Origem"),
    PARA_SIGLA_PROP("PARA_SIGLA_PROP", "Área Destino"),
    DE_TECNICO("DE_TECNICO", "Técnico Origem"),
    PARA_TECNICO("PARA_TECNICO", "Técnico Destino"),
    DE_COD_SIT("DE_COD_SIT", "Situação Origem"),
    PARA_COD_SIT("PARA_COD_SIT", "Situação Destino");

    private final String orderBy;
    private final String description;


}
