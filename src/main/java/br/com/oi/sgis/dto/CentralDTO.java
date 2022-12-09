package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CentralDTO {

    @NotBlank(message = "O código   do agrupamento não deve ser nulo")
    @Size(max = 20,message = "O código do agrupamento deve possuir no máximo 20 caracteres")
    private String id;
    @NotBlank(message = "A descrição do agrupamento não deve ser nula")
    @Size(max = 30,message = "A descrição do agrupamento deve possuir no máximo 30 caracteres")
    private String description;
    @NotBlank(message = "O prefixo do agrupamento não deve ser nulo")
    @Size(max = 5,message = "O prefixo do agrupamento deve possuir no máximo 5 caracteres")
    private String prefix;
    @NotBlank(message = "O tombamento do agrupamento não deve ser nulo")
    @Size(max = 12,message = "O tombamento do agrupamento deve possuir no máximo 12 caracteres")
    private String tipping;
    @NotNull(message = "A estação deve ser informada.")
    private StationDTO station;


}
