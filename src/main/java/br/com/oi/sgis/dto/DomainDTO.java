package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class DomainDTO {
    private String key;
    private String description;
}
