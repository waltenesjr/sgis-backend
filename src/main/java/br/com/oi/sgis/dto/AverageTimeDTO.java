package br.com.oi.sgis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@AllArgsConstructor
public class AverageTimeDTO {
    private String brNumber;
    private LocalDateTime date;
    private String situation;
    private String barcode;
    private String unityCode;
    private String description;
    private String repairCenter;
    private LocalDateTime acceptanceDate;
    private LocalDateTime closeDate;

    @JsonIgnore
    public String getDate(){
        if(date==null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }

    public String getRepairTime(){
        LocalDateTime initialTime = getInitialTime();
        long days = initialTime.until(closeDate, ChronoUnit.DAYS);
        long hours = initialTime.until(closeDate, ChronoUnit.HOURS) - days*24;
        long minutes = initialTime.until(closeDate, ChronoUnit.MINUTES) - (hours + (days*24) * 60);
        DecimalFormat df = new DecimalFormat("00");
        return df.format(days) + " d " + df.format(hours) +
                " h " + df.format(minutes) + " mi";
    }

    private LocalDateTime getInitialTime() {
        LocalDateTime initialTime = acceptanceDate;
        if(acceptanceDate==null)
            initialTime = date;
        return initialTime;
    }

    public long getTotalDurationInMinutes(){
        return getInitialTime().until(closeDate, ChronoUnit.MINUTES);
    }


}
