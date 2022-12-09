package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericQueryTypeDTO {

    private String id;
    private String description;
    private List<DomainDTO> columns;
}
