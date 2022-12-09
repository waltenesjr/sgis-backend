package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
public class DefectDTO {
    @NotBlank(message = "O código do defeito não deve ser nulo")
    @Size(max = 4,message = "O código do defeito deve possuir no máximo 4 caracteres")
    private String id;
    @NotBlank(message = "A descrição do defeito não deve ser nula")
    @Size(max = 20,message = "A descrição deve possuir no máximo 20 caracteres")
    private String description;
}
