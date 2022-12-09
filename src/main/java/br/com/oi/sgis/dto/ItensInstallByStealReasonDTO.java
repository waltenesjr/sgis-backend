package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Builder @Data
@NoArgsConstructor
@AllArgsConstructor
public class ItensInstallByStealReasonDTO {

    private LocalDateTime installationDate;
    private String reason;
    private String unityId;
    private String modelId;
    private String manufacturer;
    private String technicianId;
    private String technicianName;
    private String boNumber;
    private String accountantCompany;

    public String getInstallationDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return installationDate.format(formatter);
    }
}
