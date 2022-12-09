package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummaryItemViewDTO {
    @Column(name = "modelCode")
    private String modelCode;
    private String modelDescription;
    private String unityCode;
    private String unityDescription;
    private String mnemonic;
    private String stationCode;
    private Long total;
}
