package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class CurrencyTypeDTO {
    private String id;
    private String description;
}
