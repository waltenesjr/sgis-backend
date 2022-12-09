package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.CostComparisonRepDTO;
import br.com.oi.sgis.dto.CostComparisonRepairFilterDTO;
import br.com.oi.sgis.repository.RepairTicketRepository;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
import org.apache.catalina.LifecycleState;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CostComparisonServiceTest {

    @InjectMocks
    private CostComparisonService costComparisonService;
    @Mock
    private ReportService reportService;
    @Mock
    private RepairTicketRepository repairTicketRepository;

    @Test
    void report() throws JRException, IOException {
        List<CostComparisonRepDTO> result = new EasyRandom().objects(CostComparisonRepDTO.class, 5).collect(Collectors.toList());
        byte[] report = new byte[50];
        Mockito.doReturn(result).when(repairTicketRepository).findCostRepair(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(report).when(reportService).costComparisonReport(Mockito.any(), Mockito.any());
        CostComparisonRepairFilterDTO filterDTO = new EasyRandom().nextObject(CostComparisonRepairFilterDTO.class);
        byte[] reportReturned = costComparisonService.report(filterDTO);
        assertNotNull(reportReturned);
    }

    @Test
    void reportEmptyError() throws JRException, IOException {
        List<CostComparisonRepDTO> result = List.of();
        byte[] report = new byte[50];
        Mockito.doReturn(result).when(repairTicketRepository).findCostRepair(Mockito.any(), Mockito.any(), Mockito.any());
        CostComparisonRepairFilterDTO filterDTO = new EasyRandom().nextObject(CostComparisonRepairFilterDTO.class);
        Exception e = assertThrows(IllegalArgumentException.class, () ->costComparisonService.report(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void reportError() throws JRException, IOException {
        List<CostComparisonRepDTO> result = new EasyRandom().objects(CostComparisonRepDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(result).when(repairTicketRepository).findCostRepair(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doThrow(JRException.class).when(reportService).costComparisonReport(Mockito.any(), Mockito.any());
        CostComparisonRepairFilterDTO filterDTO = new EasyRandom().nextObject(CostComparisonRepairFilterDTO.class);
        Exception e = assertThrows(IllegalArgumentException.class, () ->costComparisonService.report(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }
}