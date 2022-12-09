package br.com.oi.sgis.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredItensDTO {
    private LocalDateTime registerDate;
    private String technicianId;
    private String technicianName;
    private String sapStatus;
    private String unityId;
    private String modelId;
    private String manufacturer;
    private String accountantCompany;
    private String numberType;
    private String number;

    public String getRegisterDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return registerDate.format(formatter);
    }
}
