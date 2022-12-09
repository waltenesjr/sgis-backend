package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.GeneralItensReportBreakEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Data
public class GeneralItensCriteriaDTO {
    private String applicationId;
    private String unityId;
    private String responsibleId;
    private String typeId;
    private String depositaryId;
    private String situationId;
    private String mnemonic;
    private String technicianId;
    private String unityCodeId;
    private String equipamentId;
    private String manufacturerId;
    private String stationId;
    private String location;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime situationInitialDate;
    private LocalDateTime situationFinalDate;
    private GeneralItensReportBreakEnum order;

    @JsonIgnore
    public String getSituationPeriod(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
       return situationInitialDate.format(formatter) + " - " + situationFinalDate.format(formatter);
    }
}
