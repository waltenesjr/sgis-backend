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
public class ApplicationDTO {
    @NotBlank(message = "O código da aplicação não deve ser nulo")
    @Size(max = 10,message = "O código da aplicação deve possuir no máximo 10 caracteres")
    private String id;
    @NotBlank(message = "A descrição da aplicação não deve ser nula")
    @Size(max = 60,message = "A descrição deve possuir no máximo 60 caracteres")
    private String description;
}
