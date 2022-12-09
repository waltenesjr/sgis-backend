package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CurrencyTypeEnum {

    RE("RE", "Real" ),
    DO("DO", "Dólar"),
    EU("EU", "Euro");

    private final String id;
    private final String description;

}
