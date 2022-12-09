package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum ReasonForWriteOffEnum {

    OBS("OBS", "Obsolescência", "4", "TRC"),
    SUC("SUC", "Sucata", "7", "TRC"),
    BXP("BXP", "Baixa para instalação no cliente", "", "BXP"),
    BXC("BXC", "Baixa por erro de cadastro", "", "BXC");

    private final String cod;
    private final String description;
    private final String reason;
    private final String situation;

    public static List<ReasonForWriteOffEnum> needsTechnicalReport(){
        return List.of(OBS,SUC);
    }

    public static ReasonForWriteOffEnum getByReason(String reason){
        return Arrays.stream(values()).filter(x -> x.getReason().equals(reason)).findFirst().orElse(null);
    }
}
