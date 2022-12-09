package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class SummaryItemCriteriaReportDTOTest {

    @Test
    void summaryItemCriteriaReportDTOTest(){
        SummaryItemCriteriaReportDTO criteriaSummaryDto = SummaryItemCriteriaReportDTO.builder().build();
        criteriaSummaryDto.setApplicationCode("123A");
        criteriaSummaryDto.setCompanyCode("123C");
        criteriaSummaryDto.setMnemonic("123 mnemonic");
        criteriaSummaryDto.setModelCode("123 model");
        criteriaSummaryDto.setTypeCode("123 type");
        criteriaSummaryDto.setResponsibleCode("123 responsible");
        criteriaSummaryDto.setUnityCode("12345");

        assertEquals("123A", criteriaSummaryDto.getApplicationCode());
        assertEquals("123C", criteriaSummaryDto.getCompanyCode());
        assertEquals("123 mnemonic", criteriaSummaryDto.getMnemonic());
        assertEquals("123 model", criteriaSummaryDto.getModelCode());
        assertEquals("123 type", criteriaSummaryDto.getTypeCode());
        assertEquals("123 responsible", criteriaSummaryDto.getResponsibleCode());
        assertEquals("12345", criteriaSummaryDto.getUnityCode());
        assertNull(criteriaSummaryDto.getStationCode());
        assertNull(criteriaSummaryDto.getBreakResults());
    }

}