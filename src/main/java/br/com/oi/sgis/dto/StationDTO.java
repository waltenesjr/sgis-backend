package br.com.oi.sgis.dto;

import br.com.oi.sgis.entity.Uf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StationDTO {
    @NotBlank(message = "A sigla de estação não deve ser nula ou em branco. ")
    @Size(max = 10,message = "A sigla de estação deve possuir no máximo 10 caracteres")
    private String id;
    @NotBlank(message = "A descrição de estação não deve ser nula ou em branco. ")
    @Size(max = 60,message = "A descrição de estação deve possuir no máximo 60 caracteres")
    private String description;
    private BigDecimal number;
    private Uf uf;
    private AddressDTO address;
}
