package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.LabelUtils;
import lombok.Builder;
import lombok.Data;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class PhysicalElectricalPropsReportDTO {
    private String barcode;
    private List<PhysicalElectricalPropsDTO> items;
    private String unityCode;
    private String responsible;
    private String station;
    private String deposit;
    private BigDecimal totalLength;

    public BufferedImage getBarcodeImage() throws OutputException, BarcodeException {
        try {
            return LabelUtils.getBarcodeImage(barcode);

        }catch (RuntimeException e){
            return null;
        }
    }
}
