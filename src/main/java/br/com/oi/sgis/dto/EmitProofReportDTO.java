package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.LabelUtils;
import lombok.Getter;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class EmitProofReportDTO {
    private BufferedImage barcodeImage;
    private String technicalId;
    private String technicalName;
    private String technicalLocation;
    private String unityId;
    private String description;
    private String barcode;
    private String department;
    private LocalDateTime date;

    public EmitProofReportDTO(String technicalId, String technicalName, String technicalLocation, String unityId,
                              String description, String barcode, String department, LocalDateTime date) {
        this.technicalId = technicalId;
        this.technicalName = technicalName;
        this.technicalLocation = technicalLocation;
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
