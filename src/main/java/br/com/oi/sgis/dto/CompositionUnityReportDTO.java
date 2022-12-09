package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompositionUnityReportDTO {
    private UnityBarcodeDTO unityModel;
    private List<UnityBarcodeDTO> unitiesItem;





}
