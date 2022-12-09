package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PriorityRepairEnum {

    B("B", "Baixa"),
    N("N", "Normal"),
    M("M", "Média"),
    A("A", "Alta");

    private final String cod;
    private final String description;
}
