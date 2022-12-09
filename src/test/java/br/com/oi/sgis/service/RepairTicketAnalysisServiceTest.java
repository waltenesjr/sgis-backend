package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.AnalyticRepairDTO;
import br.com.oi.sgis.repository.RepairTicketRepository;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.oi.sgis.enums.BreaksAnalyticRepairEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RepairTicketAnalysisServiceTest {

    @InjectMocks
    private RepairTicketAnalysisService repairTicketAnalysisService;
    @Mock
    private ReportService reportService;
    @Mock
    private RepairTicketRepository repairTicketRepository;

    private  byte[] report;
    private List<AnalyticRepairDTO> analyticRepairDTOS;
    @BeforeEach
    private void setUP() throws JRException, IOException {
        analyticRepairDTOS = new EasyRandom().objects(AnalyticRepairDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn("NENHUM").when(repairTicketRepository).externalInternalRepair(any());
        report = new byte[50];
        Mockito.doReturn(report).when(reportService).analysisRepairReport(any());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] reportToReturn = repairTicketAnalysisService.report(analyticRepairDTOS, null);
        assertEquals(report, reportToReturn);
    }

    @Test
    void reportByModel() throws JRException, IOException {
        byte[] reportToReturn = repairTicketAnalysisService.report(analyticRepairDTOS, MODEL);
        assertEquals(report, reportToReturn);
    }

    @Test
    void reportBySituation() throws JRException, IOException {
        byte[] reportToReturn = repairTicketAnalysisService.report(analyticRepairDTOS, SITUATION);
        assertEquals(report, reportToReturn);
    }

    @Test
    void reportByDefect() throws JRException, IOException {
        byte[] reportToReturn = repairTicketAnalysisService.report(analyticRepairDTOS, DEFECT);
        assertEquals(report, reportToReturn);
    }

    @Test
    void reportByRepairCenter() throws JRException, IOException {
        byte[] reportToReturn = repairTicketAnalysisService.report(analyticRepairDTOS, RC);
        assertEquals(report, reportToReturn);
    }

    @Test
    void reportByOrigin() throws JRException, IOException {
        byte[] reportToReturn = repairTicketAnalysisService.report(analyticRepairDTOS, ORIGIN);
        assertEquals(report, reportToReturn);
    }
}