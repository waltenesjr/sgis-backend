package br.com.oi.sgis.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ExternalRepairReportDTO {
    private String brNumber;
    private String barcode;
    private String unity;
    private String repairCenter;
    private String origin;
    @Setter
    private String contract;
    private String provider;
    private String estimate;
    private LocalDateTime sendDate;
    private String defect;
    @Setter
    private LocalDateTime warrantyDate;
    @Setter
    private String brWarranty;
    private LocalDateTime acceptanceDate;
    private LocalDateTime cancelDate;
    @Setter
    private String warrantyProvider;
    private LocalDateTime returnDate;
    private Long deliveryDays;
    private String situationDate;
    private String defectDescription;
    private LocalDateTime openDate;
    private LocalDateTime estimateDate;

    public ExternalRepairReportDTO(String brNumber, String barcode, String unity, String repairCenter, String origin,
                                   LocalDateTime openDate, String provider, String estimate, LocalDateTime estimateDate,
                                   LocalDateTime sendDate, String defect, String defectDescription, LocalDateTime acceptanceDate,
                                   LocalDateTime cancelDate, LocalDateTime returnDate, Long deliveryDays, String situationDate) {
        this.brNumber = brNumber;
        this.barcode = barcode;
        this.unity = unity;
        this.repairCenter = repairCenter;
        this.origin = origin;
        this.openDate = openDate;
        this.provider = provider;
        this.estimate = estimate;
        this.estimateDate = estimateDate;
        this.sendDate = sendDate;
        this.defect = defect;
        this.defectDescription = defectDescription;
        this.acceptanceDate = acceptanceDate;
        this.cancelDate = cancelDate;
        this.returnDate = returnDate;
        this.deliveryDays = deliveryDays;
        this.situationDate = situationDate;
    }

    public String getExpectedDate(){
        if(acceptanceDate==null)
            return "";
        LocalDateTime dateExpected = acceptanceDate.plusDays(deliveryDays);
        return getDateString(dateExpected);
    }

    private String getDateString(LocalDateTime dateToString) {
        if (dateToString == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateToString.format(formatter);
    }

    public String getOpenDate(){
        if (openDate==null)
            return "";
        return getDateString(openDate);
    }

    public String getEstimateDate(){
        if (estimateDate==null)
            return "";
        return getDateString(estimateDate);
    }

    public String getSendDate(){
        if (sendDate==null)
            return "";
        return getDateString(sendDate);
    }

    public String getAcceptanceDate(){
        if (acceptanceDate==null)
            return "";
        return getDateString(acceptanceDate);
    }

    public String getCancelDate(){
        if (cancelDate==null)
            return "";
        return getDateString(cancelDate);
    }

    public String getReturnDate(){
        if (returnDate==null)
            return "";
        return getDateString(returnDate);
    }

    public String getWarrantyDate(){
        if (warrantyDate==null)
            return "";
        return getDateString(warrantyDate);
    }

}
