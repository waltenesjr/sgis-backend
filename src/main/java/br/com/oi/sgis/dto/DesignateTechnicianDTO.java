package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class DesignateTechnicianDTO {
    private String barcode;
    private String technicianId;
    private String unityId;
    private Boolean unitiesWithoutTechnician;
}
