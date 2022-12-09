package br.com.oi.sgis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnityAcceptanceDTO {
    @NotNull(message = "A unidade não deve ser nula")
    private String unityId;
    private String stationId;
    private String location;
    @JsonIgnore
    private String userId;
}
