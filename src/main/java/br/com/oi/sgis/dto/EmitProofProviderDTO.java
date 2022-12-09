package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmitProofProviderDTO {

    private String responsibleId;
    @NotBlank(message = "O provedor não deve ser nulo")
    private String providerId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime initialDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finalDate;

    public String getResponsibleId() {
        if(responsibleId == null)
            return "";
        return responsibleId;
    }

    public LocalDateTime getInitialDate() {
        if(initialDate==null)
            return LocalDateTime.of(1800, 1, 1, 0, 0);
        return initialDate;
    }

    public LocalDateTime getFinalDate() {
        if(finalDate==null)
            return LocalDateTime.of(3000, 1, 1, 0, 0);
        return finalDate;
    }
}
