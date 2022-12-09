package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.MovItensReportOrderEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MovItensReportDTOTest {

    @Test
    void movItensReportDTOTest(){
        MovItensReportDTO movItensReportDTO = MovItensReportDTO.builder().build();

        movItensReportDTO.setUnityCode("12345");
        movItensReportDTO.setFromResponsible("RJ-ARC");
        movItensReportDTO.setToResponsible("MA-ARC");
        movItensReportDTO.setFromSituations(List.of("DIS"));
        movItensReportDTO.setToSituations(List.of("DEF"));
        movItensReportDTO.setToTechnician("RJ15454");
        movItensReportDTO.setFromTechnician("RJ15454");
        movItensReportDTO.setInitialPeriod(LocalDateTime.of(2021, 2,14,0,0));
        movItensReportDTO.setEndPeriod(LocalDateTime.of(2022, 2,14,0,0));

        assertEquals("12345", movItensReportDTO.getUnityCode());
        assertEquals("RJ-ARC", movItensReportDTO.getFromResponsible());
        assertEquals("MA-ARC", movItensReportDTO.getToResponsible());
        assertEquals(1, movItensReportDTO.getFromSituations().size());
        assertTrue(movItensReportDTO.getToSituations().contains("DEF"));
        assertEquals("RJ15454", movItensReportDTO.getToTechnician());
        assertEquals("RJ15454", movItensReportDTO.getFromTechnician());
        assertFalse(movItensReportDTO.isBreakTotals());
        assertEquals(MovItensReportOrderEnum.DATA, movItensReportDTO.getOrderBy());
        assertTrue(movItensReportDTO.getInitialPeriod().isBefore(LocalDateTime.now()));
        assertEquals(LocalDateTime.of(2022, 2,14,0,0), movItensReportDTO.getEndPeriod());


    }

}