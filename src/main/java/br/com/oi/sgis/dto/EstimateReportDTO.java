package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.LabelUtils;
import lombok.Builder;
import lombok.Data;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder @Data
public class EstimateReportDTO {
    private String id;
    private String company;
    private String companyDescription;
    private String contract;
    private BigDecimal totalValue;
    private BigDecimal totalWeight;
    private LocalDateTime date;
    private LocalDateTime sendDate;
    private String fiscalNote;
    private LocalDateTime warrantyDate;
    private String observation;
    private String phone;
    private String contact;
    private Long deliveryDays;
    private Long warrantyDays;
    private String department;
    private AddressDTO address;
    private List<EstimateItemReportDTO> items;

    public BufferedImage getEstimateNumberImage() throws OutputException, BarcodeException {
        try {
            return LabelUtils.getBarcode128A(id);

        }catch (RuntimeException e){
            return null;
        }
    }

    private DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public String getDate() {
        DateTimeFormatter formatter = getDateTimeFormatter();
        if(date!=null)
            return date.format(formatter);
        return null;
    }

    public String getAddress() {
        if(address!=null)
            return address.getAddressDescription() +  " \n" +
                    address.getDistrict() + " - " + address.getCity() + " - " + address.getUf().getId() + " " +
                    "\nCEP " + address.getCep();
        return null;
    }

    public String getSendDate() {
        DateTimeFormatter formatter = getDateTimeFormatter();
        if(sendDate!=null)
            return sendDate.format(formatter);
        return null;
    }

    public String getWarrantyDate() {
        DateTimeFormatter formatter = getDateTimeFormatter();
        if(warrantyDate!=null)
            return warrantyDate.format(formatter);
        return null;
    }

}
