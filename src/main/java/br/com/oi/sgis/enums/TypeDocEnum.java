package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum TypeDocEnum {
    TXT("TXT", ".txt", "Texto" ),
    XLSX("XLSX", ".xlsx","Excel"),
    PDF("PDF", ".pdf","Pdf");

    private final String cod;
    private final String type;
    private final String description;

    public static List<TypeDocEnum> pdfXlsx(){
        return List.of(PDF, XLSX);
    }

    public static List<TypeDocEnum> txtXlsx(){
        return List.of(XLSX, TXT);
    }
}
