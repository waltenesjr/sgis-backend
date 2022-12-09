package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.BreaksAnalyticRepairEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder @Data
public class RepairAnalyticFilterDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime initialRepairDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finalRepairDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime initialWarrantyDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finalWarrantyDate;
    private String repairCenter;
    private String unityCode;
    private String situation;
    private String originDepartment;
    private String defect;
    private BreaksAnalyticRepairEnum breakTotals;

}
