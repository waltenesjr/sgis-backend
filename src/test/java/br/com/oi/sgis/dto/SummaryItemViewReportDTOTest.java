package br.com.oi.sgis.dto;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SummaryItemViewReportDTOTest {

    @Test
    void summaryItemViewReportDTOTest(){

        List<SummaryItemViewDTO> itens = new EasyRandom().objects(SummaryItemViewDTO.class,5).collect(Collectors.toList());
        SummaryItemViewReportDTO summaryItemViewReportDTO = SummaryItemViewReportDTO.builder().build();
        summaryItemViewReportDTO.setTypeGroup("ESTACAO");
        summaryItemViewReportDTO.setTotalItens(5L);
        summaryItemViewReportDTO.setGroupBy("Estacao 123");
        summaryItemViewReportDTO.setGroupItens(itens);

        assertEquals(5L, summaryItemViewReportDTO.getTotalItens());
        assertEquals("ESTACAO", summaryItemViewReportDTO.getTypeGroup());
        assertEquals("Estacao 123", summaryItemViewReportDTO.getGroupBy());
        assertEquals(itens.size(), summaryItemViewReportDTO.getGroupItens().size());
    }
}