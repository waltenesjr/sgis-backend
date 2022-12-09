package br.com.oi.sgis.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnityHistoricalViewDTO {

    private Long id;
    private String barcode;
    private LocalDateTime date;
    private String fromSeriesNumber;
    private String toSeriesNumber;
    private String fromSituationCode;
    private String toSituationCode;
    private String fromResponsible;
    private String toResponsible;
    private String fromCentralCode;
    private String toCentralCode;
    private String fromStationCode;
    private String toStationCode;
    private String fromAddressCode;
    private String toAddressCode;
    private String fromBoxCode;
    private String toBoxCode;
    private String fromUnityCode;
    private String toUnityCode;
    private String fromProviderResponsible;
    private String toProviderResponsible;
    private String fromDeposit;
    private String toDeposit;
    private String fromTechnician;
    private String toTechnician;
    private String fromDeadline;
    private String toDeadline;
    private String fromDestination;
    private String toDestination;
    private String fromWarranty;
    private String toWarranty;
    private String fromValue;
    private String toValue;
    private String fromTpNumber;
    private String toTpNumber;
    private String fromInstallationRack;
    private String toInstallationRack;
    private String fromClient;
    private String toClient;
    private String fromTransferDoc;
    private String toTransferDoc;
    private String fromBarcodeParent;
    private String toBarcodeParent;
    private String fromAdjustDate;
    private String toAdjustDate;
    private String fromLocation;
    private String toLocation;
    private String fromSituationDateChange;
    private String toSituationDateChange;
    private String fromTipping;
    private String toTipping;
    private String fromObservation;
    private String toObservation;

    public String getDate(){
        if(date ==null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
}
