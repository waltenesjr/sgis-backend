package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ModelDTO {

    private Long modelCod;
    private String description;
    private String manufacturerCod;
    private LocalDateTime inserctionDate;
    private String manufacturerDescription;
}
