package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.LabelUtils;
import lombok.Getter;
import lombok.Setter;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.awt.image.BufferedImage;
@Getter
public class TrackingRecordDTO {
    @Setter
    private String sequence;
    private String barcode;
    private String serieNumber;
    private String unityCode;
    private String mnemonic;
    private String description;
    private String descriptionTC;
    private String company;
    private String accountantCompany;
    private String activeClass;
    private BufferedImage barcodeImage;


    public TrackingRecordDTO(String barcode, String serieNumber, String unityCode,String mnemonic,  String description,
                                      String descriptionTC, String company, String accountantCompany, String activeClass) {
        this.barcode = barcode;
        this.serieNumber = serieNumber;
        this.unityCode = unityCode;
        this.description = description;
        this.descriptionTC = descriptionTC;
        this.company = company;
        this.mnemonic = mnemonic;
        this.accountantCompany = accountantCompany;
        this.activeClass = activeClass;
    }

    public BufferedImage getBarcodeImage() throws OutputException, BarcodeException {
        try {
            return LabelUtils.getBarcodeImage(barcode);

        }catch (RuntimeException e){
            return null;
        }
    }

    public String getAccountantCompany() {
        if(accountantCompany != null)
            return accountantCompany + " - " + activeClass;
        return null;
    }
}
