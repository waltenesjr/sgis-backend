package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.SummaryItemCriteriaReportDTO;
import br.com.oi.sgis.dto.SummaryItemViewDTO;
import br.com.oi.sgis.enums.ItensSumaryReportBreakEnum;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SummaryItemServiceTest {

    @InjectMocks
    private SummaryItemService summaryItemService;

    @Mock
    private UnityRepository unityRepository;

    @Mock
    private ReportService reportService;

    @Mock
    private Validator validator;

    private  SummaryItemCriteriaReportDTO criteria;
    private List<SummaryItemViewDTO> itens;
    @BeforeEach
    void setUp(){
        criteria = SummaryItemCriteriaReportDTO.builder().build();
        itens =  new EasyRandom().objects(SummaryItemViewDTO.class, 5).collect(Collectors.toList());
    }

    @Test
    void listItens() throws JRException, IOException {
        Mockito.doReturn(itens).when(unityRepository).findBySummaryParams(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillSummaryItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        byte[] report = summaryItemService.report(criteria, TypeDocEnum.TXT);

        assertNotNull(report);

    }

    @Test
    void listItensEmpty(){
        Mockito.doReturn(List.of()).when(unityRepository).findBySummaryParams(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> summaryItemService.report(criteria, TypeDocEnum.TXT));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }
    @Test
    void listItensGroupByStation() throws JRException, IOException {
        criteria.setBreakResults(ItensSumaryReportBreakEnum.ESTACAO);
        Mockito.doReturn(itens).when(unityRepository).findBySummaryParams(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillSummaryItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        byte[] report = summaryItemService.report(criteria, TypeDocEnum.TXT);

        assertNotNull(report);
    }

    @Test
    void listItensGroupByUnity() throws JRException, IOException {
        criteria.setBreakResults(ItensSumaryReportBreakEnum.UNIDADE);
        Mockito.doReturn(itens).when(unityRepository).findBySummaryParams(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillSummaryItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        byte[] report = summaryItemService.report(criteria, TypeDocEnum.TXT);

        assertNotNull(report);
    }

    @Test
    void listItensGroupByEquipament() throws JRException, IOException {
        criteria.setBreakResults(ItensSumaryReportBreakEnum.EQUIPAMENTO);
        Mockito.doReturn(itens).when(unityRepository).findBySummaryParams(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillSummaryItensReport(Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any());
        byte[] report = summaryItemService.report(criteria, TypeDocEnum.TXT);

        assertNotNull(report);
    }
}