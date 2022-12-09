package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.ItensSumaryReportBreakEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SummaryItemCriteriaReportDTO {
    private String applicationCode;
    private String typeCode;
    private String modelCode;
    private String unityCode;
    private String companyCode;
    private String responsibleCode;
    private String mnemonic;
    private String stationCode;
    private ItensSumaryReportBreakEnum breakResults;

}
