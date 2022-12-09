package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechniqueTypeEnum {

    FIXA("FIXA", "TLM"),
    MOVEL("MOVEL", "SMPE"),
    TRDFTTH("TRDFTTH", "MRED");

    private final String cod;
    private final String description;
}
