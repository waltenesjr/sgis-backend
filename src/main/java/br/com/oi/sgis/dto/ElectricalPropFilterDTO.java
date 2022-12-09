package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class ElectricalPropFilterDTO {
    private String equipmentType;
    private String equipmentModel;
    private String modelUnity;
    private String barcode;
    private String responsible;
}
