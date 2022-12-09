package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;

@Builder @Data
public class TechnicianTimesReportDTO {

    private List<TechnicianTimesDTO> items;
    private String technician;
    private String technicianName;
    private String department;
    private long totalHours;
    private long totalMinutes;
    private long totalSeconds;

    public String getTotal(){
        Duration duration = Duration.ZERO;
        duration = duration.plusSeconds(totalSeconds);
        duration = duration.plusMinutes(totalMinutes);
        duration = duration.plusHours(totalHours);
        DecimalFormat df = new DecimalFormat("00");
        return duration.toHours() + ":" + df.format(duration.toMinutesPart()) +
                                 ":" + df.format(duration.toSecondsPart());
    }





}

