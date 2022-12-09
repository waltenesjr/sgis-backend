package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum InstallationReasonEnum {
    IAP("IAP", "Ampliação da planta", ""),
    IRP("IRP", "Reposição", ""),
    IFM("IFM", "Furto com reposição por mesmo modelo", "9"),
    IFD("IFD", "Furto com reposição por modelo diferente", ""),
    IIN("IIN", "Incêndio com reposição por mesmo modelo", "10"),
    IAC("IAC", "Acidente com reposição por mesmo modelo", "11"),
    IID("IID", "Incêndio com reposição por modelo diferente", ""),
    IAD("IAD", "Acidente com reposição por modelo diferente", "");

    private final String cod;
    private final String description;
    private final String lostReason;

    public static List<InstallationReasonEnum> incidentsAndBurning(){
        return List.of(IIN, IAC, IID, IAD);
    }

    public static List<InstallationReasonEnum> steals(){
        return List.of(IFM, IFD);
    }

    public static InstallationReasonEnum getByLostReason(String reason){
        return Arrays.stream(values()).filter(x -> x.getLostReason().equals(reason)).findFirst().orElse(null);
    }
}
