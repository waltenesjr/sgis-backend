package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder @Data @AllArgsConstructor
public class UserExtractionReportDTO {

    private String ndsLogin;
    private String technician;
    private String technicianName;
    private Boolean status;
    private String profile;
    private String company;
    private String department;
    private String phone;
    private String email;

    public String getStatus() {
        return status ? "ATIVO" : "INATIVO";
    }
}
