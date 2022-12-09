package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.*;
import br.com.oi.sgis.service.*;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @InjectMocks
    private ReportController reportController;

    @Mock
    private MovItensService movItensService;
    @Mock
    private SummaryItemService summaryItemService;
    @Mock
    private ItemBySituationService itemBySituationService;
    @Mock
    private StockSummaryService stockSummaryService;
    @Mock
    private ItensInstallByStealReasonService itensInstallByStealReasonService;
    @Mock
    private GeneralItensService generalItensService;
    @Mock
    private RegisteredItensService registeredItensService;

    @Test
    void orderByList(){
        List<ReporterOrderDTO> orders = reportController.orderByList();
        assertEquals(MovItensReportOrderEnum.values().length,orders.size());
    }

    @Test
    void movementReport() throws JRException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(movItensService).report(Mockito.any(MovItensReportDTO.class), Mockito.any());

        MovItensReportDTO movItensReportDTO = new EasyRandom().nextObject(MovItensReportDTO.class);
        ResponseEntity<byte[]> responseReposrt = reportController.movementReport(movItensReportDTO, TypeDocEnum.TXT);

        assertEquals(HttpStatus.OK, responseReposrt.getStatusCode());
        assertEquals(report, responseReposrt.getBody());
    }

    @Test
    void groupList(){
        List<ReporterOrderDTO> orders = reportController.groupList();
        assertEquals(ItensSumaryReportBreakEnum.values().length,orders.size());
    }

    @Test
    void summaryItemReport()  {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(summaryItemService).report(Mockito.any(SummaryItemCriteriaReportDTO.class), Mockito.any());

        SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO = new EasyRandom().nextObject(SummaryItemCriteriaReportDTO.class);
        ResponseEntity<byte[]> responseReport = reportController.summaryItemReport(summaryItemCriteriaReportDTO, TypeDocEnum.TXT);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void typeList() {
        List<ReporterOrderDTO> orders = reportController.typeList();
        assertEquals(ItemBySitReportTypeEnum.values().length,orders.size());
    }

    @Test
    void situationItemReport() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(itemBySituationService).report(Mockito.any(ItemBySitReportCriteriaDTO.class), Mockito.any());

        ItemBySitReportCriteriaDTO itemBySitReportCriteriaDTO = new EasyRandom().nextObject(ItemBySitReportCriteriaDTO.class);
        ResponseEntity<byte[]> responseReport = reportController.situationItemReport(itemBySitReportCriteriaDTO, TypeDocEnum.TXT);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void filterList() {
        List<FilteringDTO> filteringDTOS = reportController.filterList();
        assertEquals(FilteringEnum.values().length,filteringDTOS.size());
    }

    @Test
    void stockSummaryReport() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(stockSummaryService).report(Mockito.any(StockSummaryCriteriaDTO.class), Mockito.any());

        StockSummaryCriteriaDTO stockSummaryCriteriaDTO = new EasyRandom().nextObject(StockSummaryCriteriaDTO.class);
        ResponseEntity<byte[]> responseReport = reportController.stockSummaryReport(stockSummaryCriteriaDTO, TypeDocEnum.XLSX);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void reasonsSteal() {
        List<InstallationReasonDTO> reasons = reportController.reasonsSteal();
        assertEquals(InstallationReasonEnum.steals().size(), reasons.size());
    }

    @Test
    void installStealsReport() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(itensInstallByStealReasonService).report(Mockito.any(), Mockito.any());

        ItensInstallByStealReasonCriteriaDTO itensInstallByStealReasonCriteriaDTO = new EasyRandom().nextObject(ItensInstallByStealReasonCriteriaDTO.class);
        ResponseEntity<byte[]> responseReport = reportController.installStealsReport(itensInstallByStealReasonCriteriaDTO, TypeDocEnum.TXT);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());

    }

    @Test
    void generalOrderList() {
        List<ReporterOrderDTO> reasons = reportController.generalOrderList();
        assertEquals(GeneralItensReportBreakEnum.values().length, reasons.size());
    }

    @Test
    void generalItens() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(generalItensService).report(Mockito.any(), Mockito.any());

        GeneralItensCriteriaDTO criteriaDTO = new EasyRandom().nextObject(GeneralItensCriteriaDTO.class);
        ResponseEntity<byte[]> responseReport = reportController.generalItens(criteriaDTO, TypeDocEnum.TXT);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());

    }

    @Test
    void registeredItens() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(registeredItensService).report(Mockito.any(), Mockito.any());
        RegisteredItensCriteriaDTO criteriaDTO = new EasyRandom().nextObject(RegisteredItensCriteriaDTO.class);
        ResponseEntity<byte[]> responseReport = reportController.registeredItens(criteriaDTO, TypeDocEnum.TXT);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }
}