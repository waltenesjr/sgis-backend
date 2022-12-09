package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BreaksAnalyticRepairEnum {
    MODEL("MODEL", "Modelo"),
    SITUATION("SITUATION", "Situação"),
    DEFECT("DEFECT", "Defeito"),
    RC("RC", "Centro de reparo"),
    ORIGIN("ORIGIN", "Dept. Origem");


    private final String breaks;
    private final String description;
}
