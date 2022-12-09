package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.LabelUtils;
import lombok.Getter;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.awt.image.BufferedImage;

@Getter
public class OrderServiceDTO {
    private BufferedImage barcodeImage;
    private String brNumber;
    private String technicalId;
    private String technicalName;
    private String barcode;
    private String barcodeParent;
    private String unityId;
    private String description;
    private String department;
    private String defectId;
    private String defectDescription;

    public OrderServiceDTO(String brNumber, String technicalId, String technicalName, String barcode,String barcodeParent,
                           String unityId, String description,  String department, String defectId,
                               String defectDescription) {
        this.brNumber = brNumber;
        this.technicalId = technicalId;
        this.technicalName = technicalName;
        this.barcode = barcode;
        this.barcodeParent = barcodeParent;
        this.unityId = unityId;
        this.description = description;
        this.department = department;
        this.defectId = defectId;
        this.defectDescription = defectDescription;
    }

    public BufferedImage getBarcodeImage() throws OutputException, BarcodeException {
        try {
            return LabelUtils.getBarcodeImage(barcode);

        }catch (RuntimeException e){
            return null;
        }
    }

}
