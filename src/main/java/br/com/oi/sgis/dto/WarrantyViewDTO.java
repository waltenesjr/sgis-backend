package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WarrantyViewDTO {
    private String unityId;
    private String provider;
    private String brNumber;
    private String contract;
    private String purchaseDoc;
    private LocalDateTime warrantyDate;
}
