package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CableMovementFilterDTO {

    private String barcode;
    private LocalDateTime date;
    private String movementType;
    private String propertyId;



}
