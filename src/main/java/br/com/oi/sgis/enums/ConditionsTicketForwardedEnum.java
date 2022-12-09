package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum ConditionsTicketForwardedEnum {
    ABE("ABE", "Abertos e não aceitos", List.of("ABE")),
    ACT("ACT", "Aceitos e  encaminhados", List.of("ACT","EGA","EGC","EGO","ERI","ECT","EOR"));


    private final String condition;
    private final String description;
    private final List<String> situations;
}
