package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.LabelUtils;
import lombok.Getter;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class EmitProofProviderReportDTO {
    private BufferedImage barcodeImage;
    private String providerId;
    private String providerName;
    private String clientId;
    private String clientName;
    private String department;
    private String unityId;
    private String description;
    private String barcode;
    private LocalDateTime date;

    public EmitProofProviderReportDTO(String providerId, String providerName, String clientId, String clientName,String department,  String unityId,
                                      String description, String barcode, LocalDateTime date) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.clientId = clientId;
        this.clientName = clientName;
        this.unityId = unityId;
        this.description = description;
        this.barcode = barcode;
        this.department = department;
        this.date = date;
    }

    public BufferedImage getBarcodeImage() throws OutputException, BarcodeException {
        try {
            return LabelUtils.getBarcodeImage(barcode);

        }catch (RuntimeException e){
            return null;
        }
    }

    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }
}
