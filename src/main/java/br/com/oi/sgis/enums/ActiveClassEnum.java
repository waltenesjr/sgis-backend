package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActiveClassEnum {
    ZETSOBRE("ZETSOBRE"),
    ZXFERINS("ZXFERINS"),
    ZPFERINS("ZPFERINS"),
    ZPSOBRES("ZPSOBRES");

    private final String cod;
}
