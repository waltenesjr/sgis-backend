package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.LabelUtils;
import lombok.Getter;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class TechnicianTicketDTO {
    private BufferedImage barcodeImage;
    private String brNumber;
    private String technicalId;
    private String technicalName;
    private String unityId;
    private String description;
    private String barcode;
    private String department;
    private String departmentCR;
    private String defectId;
    private String defectDescription;
    private LocalDateTime date;

    public TechnicianTicketDTO(String brNumber, String technicalId, String technicalName,  String unityId,
                              String description, String barcode, String department, String departmentCR, String defectId,
                               String defectDescription, LocalDateTime date) {
        this.brNumber = brNumber;
        this.technicalId = technicalId;
        this.technicalName = technicalName;
        this.unityId = unityId;
        this.description = description;
        this.barcode = barcode;
        this.department = department;
        this.departmentCR = departmentCR;
        this.defectId = defectId;
        this.defectDescription = defectDescription;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
}
