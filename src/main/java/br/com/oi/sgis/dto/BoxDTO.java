package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoxDTO {
    private String id;
    private String abbreviation ;
    private String description;
    private String location;
    private String boxType;
    private String station;
    private boolean active;

}
