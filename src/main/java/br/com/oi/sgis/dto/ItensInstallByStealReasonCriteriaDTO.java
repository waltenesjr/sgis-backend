package br.com.oi.sgis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
@Data
public class ItensInstallByStealReasonCriteriaDTO {
    private String responsibleCode;
    @NotEmpty(message = "Favor, selecione pelo menos um motivo de instalação!")
    private List<InstallationReasonDTO> installationReason;
    @NotNull(message = "Data inválida, informe um período!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime initialPeriod;
    @NotNull(message = "Data inválida, informe um período!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finalPeriod;

    @JsonIgnore
    public String getPeriod(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return initialPeriod.format(formatter) +
                " - " + finalPeriod.format(formatter);
    }
}
