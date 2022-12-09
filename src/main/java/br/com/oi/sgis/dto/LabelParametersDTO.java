package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.LabelPrintTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LabelParametersDTO {

    private String lastEmittedLabel;
    private Long labelQuantity;
    @Min(1) @Max(3)
    private Integer labelByLine;
    private Long topMargin;
    private Long lateralMargin;
    private Long labelHeight;
    private Long labelWidth;
    private Long verticalRange;
    private Long horizontalRange;
    private LabelPrintTypeEnum printType;
    private Boolean onlyTest;

    public LabelParametersDTO(PackingLabelParametersDTO parametersDTO){
        this.labelQuantity = parametersDTO.getLabelQuantity();
        this.labelByLine = parametersDTO.getLabelByLine();
        this.topMargin = parametersDTO.getTopMargin();
        this.lateralMargin = parametersDTO.getLateralMargin();
        this.labelHeight = parametersDTO.getLabelHeight();
        this.labelWidth = parametersDTO.getLabelWidth();
        this.verticalRange = parametersDTO.getVerticalRange();
        this.horizontalRange = parametersDTO.getHorizontalRange();
        this.onlyTest = parametersDTO.getInhibitBarcode();
    }
}
