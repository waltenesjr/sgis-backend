package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CableMovementQueryDTO {

    @NotBlank(message = "O código de barras deve ser informado")
    private String barcode;
    private LocalDateTime initialDate;
    private LocalDateTime finalDate;
    @NotBlank(message = "A propriedade elétrica deve ser informada")
    private String propertyId;



}
