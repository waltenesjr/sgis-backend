package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.awt.image.BufferedImage;

@Builder @Data
public class LabelReportDTO
{
    private BufferedImage column1;
    private BufferedImage column2;
    private BufferedImage column3;
    private String barcode1;
    private String barcode2;
    private String barcode3;
}
