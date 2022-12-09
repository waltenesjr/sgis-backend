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
public class ItemEstimatesAnalysisReportDTO {
    private String company;
    private String companyName;
    private List<ItemsAnalysis> itemsAnalyses;
    private Integer totalToRepair;
    private Integer totalItens;
    private Integer totalValue;

    @Builder @Data
    public static class ItemsAnalysis {
        private String barcode;
        private String serieNumber;
        private String brNumber;
        private String description;
        private String estimate;
        private String department;
        private String provider;
        private String fiscalNote;
        private LocalDateTime fiscalNoteDate;
        private LocalDateTime date;
        private boolean analyzed;
        private BigDecimal value;
        private BigDecimal repairValue;
        private String company;
        private String companyName;

        public BufferedImage getBarcodeImage() throws OutputException, BarcodeException {
            try {
                return LabelUtils.getBarcodeImage(barcode);

            }catch (RuntimeException e){
                return null;
            }
        }

        public String getDate() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if(date !=null)
                return date.format(formatter);
            return null;
        }

        public String getFiscalNoteDate() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if(fiscalNoteDate !=null)
                return fiscalNoteDate.format(formatter);
            return null;
        }

        public String getAnalyzed(){
            if(analyzed)
                return "S";
            return "N";
        }
    }

}
