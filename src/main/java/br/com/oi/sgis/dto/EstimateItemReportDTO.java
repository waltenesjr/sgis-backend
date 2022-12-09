package br.com.oi.sgis.dto;


import br.com.oi.sgis.util.LabelUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor @Getter
public class EstimateItemReportDTO {
    private String estimateNumber;
    private Long sequence;
    private BigDecimal value;
    private String ticketSituation;
    private String interventionSituation;
    private String brNumber;
    private String barcode;
    private String unityId;
    private String costCenter;
    private String equipamentCostCenter;
    private String unityDescription;
    private String departmentOrigin;
    private String mnemonic;
    private String techCode;
    private String interventionCode;
    private String interventionDescription;
    private String technicianId;
    private String interventionObs;
    private String operatorId;
    private String ticketDescription;
    private String defectId;
    private String defectDescription;
    private LocalDateTime date;
    private String provider;
    private LocalDateTime warranty;
    private BigDecimal weight;
    private BigDecimal areaValue;
    private LocalDateTime approvalDate;
    private LocalDateTime cancelDate;
    private String serieNumber;
    private String fiscalClassification;
    private String ticketSituationDesc;

    public String getUnityDescription() {
        return unityId + " - " + unityDescription + " - "+ mnemonic;
    }

    public BufferedImage getBarcodeImage() throws OutputException, BarcodeException {
        try {
            return LabelUtils.getBarcodeImage(barcode);

        }catch (RuntimeException e){
            return null;
        }
    }

    public String getDate() {
        DateTimeFormatter formatter = getDateTimeFormatter();
        return date.format(formatter);
    }

    private DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public String getWarranty() {
        DateTimeFormatter formatter = getDateTimeFormatter();
        if(warranty!=null)
            return warranty.format(formatter);
        return "";
    }

    public String getApprovalDate() {
        DateTimeFormatter formatter = getDateTimeFormatter();
        return approvalDate.format(formatter);
    }

    public String getCancelDate() {
        DateTimeFormatter formatter = getDateTimeFormatter();
        if(cancelDate!= null)
            return cancelDate.format(formatter);
        return "";
    }
}
