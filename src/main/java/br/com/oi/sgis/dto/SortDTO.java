package br.com.oi.sgis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortDTO {

    @JsonProperty("property")
    private String property;
    @JsonProperty("order")
    private String order;

    public SortDTO(Sort.Order sort) {
        this.property = sort.getProperty();
        this.order = sort.getDirection().name();
    }
}
