package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SummaryItemViewDTOTest {

    @Test
    void summaryItemViewDTOTest(){
        SummaryItemViewDTO summaryItemViewDTO = SummaryItemViewDTO.builder().build();
        summaryItemViewDTO.setMnemonic("12345");
        summaryItemViewDTO.setModelCode("123 M");
        summaryItemViewDTO.setModelDescription("123 M");
        summaryItemViewDTO.setStationCode("ST123");
        summaryItemViewDTO.setUnityCode("1235");
        summaryItemViewDTO.setUnityDescription("1235 Description");
        summaryItemViewDTO.setTotal(5L);

        assertEquals("12345",summaryItemViewDTO.getMnemonic());
        assertEquals("123 M",summaryItemViewDTO.getModelCode());
        assertEquals("123 M",summaryItemViewDTO.getModelDescription());
        assertEquals("ST123",summaryItemViewDTO.getStationCode());
        assertEquals("1235",summaryItemViewDTO.getUnityCode());
        assertEquals("1235 Description",summaryItemViewDTO.getUnityDescription());
        assertEquals(5L, summaryItemViewDTO.getTotal());
    }

}