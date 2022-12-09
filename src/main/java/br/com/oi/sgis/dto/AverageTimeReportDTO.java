package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;

@Builder
@Data
public class AverageTimeReportDTO {
    private List<AverageTimeDTO> items;
    private String unityCode;
    private String description;
    private long totalItems;
    private long totalMinutes;

    public String getAverageTime(){
        Duration duration = Duration.ZERO;
        duration = duration.plusMinutes(totalMinutes/totalItems);
        DecimalFormat df = new DecimalFormat("00");
        return duration.toDays() + " d " + df.format(duration.toHoursPart()) +
                " h " + df.format(duration.toMinutesPart()) + " mi";
    }

    public long getMediaMinutes(){
        return totalMinutes/totalItems;
    }
}
