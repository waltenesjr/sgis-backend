package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class UnitExtractionDTO {
    private String situation;
    private String responsible;
    private String uf;
    private String technic;
}
