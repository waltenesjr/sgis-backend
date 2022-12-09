package br.com.oi.sgis.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class SapOperationHistoryReportDTO {

    private String operation;

    private String unityId;

    private LocalDateTime processingDate;

    private String messageCode;

    private String message;

    private String description;

    public SapOperationHistoryReportDTO(String operation, String unityId, LocalDateTime processingDate, String messageCode, String message, String description) {
        this.operation = operation;
        this.unityId = unityId;
        this.processingDate = processingDate;
        this.messageCode = messageCode;
        this.message = message;
        this.description = description;
    }

    public String getProcessingDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return processingDate.format(formatter);
    }
}
