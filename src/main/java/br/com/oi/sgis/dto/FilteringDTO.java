package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class FilteringDTO {
    private String filter;
    private String description;
}
