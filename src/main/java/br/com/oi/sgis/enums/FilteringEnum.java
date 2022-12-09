package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum FilteringEnum {

    TUDO("TUDO", "Tudo"),
    MAXIMO("MAXIMO", "Estoque Máximo"),
    REPOSICAO("REPOSICAO", "Ponto Reposição"),
    MINIMO("MINIMO", "Estoque Minímo");


    private final String filter;
    private final String description;
}
