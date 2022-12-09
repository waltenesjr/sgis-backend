package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferProviderDTO {
    @NotBlank(message = "O código da barras não deve ser nulo")
    @Size(max = 40,message = "O código da barras deve possuir no máximo 40 caracteres")
    private String barcode;
    @NotBlank(message = "O prestador não deve ser nulo")
    private String providerId;
    private Boolean generatePendency;
}
