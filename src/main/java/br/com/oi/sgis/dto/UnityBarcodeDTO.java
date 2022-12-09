package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.LabelUtils;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.awt.image.BufferedImage;

public class UnityBarcodeDTO {
    private String id;
    private String description;
    private BufferedImage barcodeImage;
    private String situation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BufferedImage getBarcodeImage() throws OutputException, BarcodeException {
        try {
            return LabelUtils.getBarcodeImage(id);

        }catch (RuntimeException e){
            return null;
        }
    }
    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public void setBarcodeImage(String id) throws BarcodeException, OutputException {
        try {
            this.barcodeImage = LabelUtils.getBarcodeImage(id);

        }catch (RuntimeException e){
            this.barcodeImage = null;
        }
    }

}
