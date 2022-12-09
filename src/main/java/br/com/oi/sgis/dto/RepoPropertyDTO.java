package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class RepoPropertyDTO {
    private String idUser;
    @NotBlank(message = "A unidade não pode ser nula")
    private String idUnity;
    @NotBlank(message = "A estação não pode ser nula")
    private String idStation;
    private String location;
    private String originUf;
}
