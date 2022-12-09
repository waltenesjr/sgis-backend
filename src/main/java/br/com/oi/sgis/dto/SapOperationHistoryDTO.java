package br.com.oi.sgis.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class SapOperationHistoryDTO {

    private String unityId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime initialDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finalDate;

    private String spareCenterId;

    private String search;

    private String situation;

    public SapOperationHistoryDTO(String unityId, LocalDateTime initialDate, LocalDateTime finalDate, String spareCenterId, String search, String situation) {
        this.unityId = unityId;
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.spareCenterId = spareCenterId;
        this.search = search;
        this.situation = situation;
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

    public String getSearch(){
        if(search == null)
            return "";
        return search;
    }
}
