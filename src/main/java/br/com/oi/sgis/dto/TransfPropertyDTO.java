package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @Builder
public class TransfPropertyDTO {
    private String idUser;
    private String idUnity;
    @NotNull(message = "O destino não pode ser nulo")
    private String idDepDestination;
    private String pendency;
}
