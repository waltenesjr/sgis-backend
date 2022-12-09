package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Builder @Data @AllArgsConstructor
public class TechnicianTimesDTO {
    private String technician;
    private String barcode;
    private String technicianName;
    private String departmentTechnician;
    private String modelEquipment;
    private String unityCode;
    private String description;
    private LocalDateTime initialDate;
    private LocalDateTime finalDate;
    private String intervention;
    private String interventionDescription;

    public String getInitialDate() {
        return getDate(initialDate);
    }

    public String getDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return date.format(formatter);
    }
    public String getFinalDate() {
        return getDate(finalDate);
    }

    public String getTotalTime(){
        long hours = getHours();
        long minutes = getMinutes();
        long seconds = getSeconds();
        DecimalFormat df = new DecimalFormat("00");
        return hours + ":" + df.format(minutes) + ":" + df.format(seconds);
    }

    public long getHours(){
        return initialDate.until(finalDate, ChronoUnit.HOURS);
    }

    public long getMinutes(){
        long minutes = initialDate.until(finalDate, ChronoUnit.MINUTES);
        return minutes - (getHours()*60);
    }

    public long getSeconds(){
        long seconds = initialDate.until(finalDate, ChronoUnit.SECONDS);
        return seconds - (((getHours()*60) + getMinutes())*60);
    }

}
