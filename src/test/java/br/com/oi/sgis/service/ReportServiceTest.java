package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.util.ReportUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Test
    void fillItensBySteal() throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/itensByStealReasonReport.jrxml");
        ItensInstallByStealReasonReportDTO movItensReportDTO = new EasyRandom().nextObject(ItensInstallByStealReasonReportDTO.class);
        byte[] source = reportService.fillItensBySteal(List.of(movItensReportDTO), jasperReport, new HashMap<>(), TypeDocEnum.XLSX);
        assertNotNull(source);
    }


    @Test
    void fillStockSummaryReport() throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/stockSummaryReport.jrxml");
        StockSummaryReportDTO itensInstallByStealReasonReportDTO = new EasyRandom().nextObject(StockSummaryReportDTO.class);
        byte[] source = reportService.fillStockSummaryReport(List.of(itensInstallByStealReasonReportDTO), jasperReport, new HashMap<>(), TypeDocEnum.XLSX);
        assertNotNull(source);
    }

    @Test
    void fillSummaryItensReport() throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/summaryItemReport.jrxml");
        SummaryItemViewReportDTO summaryItemViewReportDTO = new EasyRandom().nextObject(SummaryItemViewReportDTO.class);
        byte[] source = reportService.fillSummaryItensReport(List.of(summaryItemViewReportDTO), jasperReport, new HashMap<>(), TypeDocEnum.XLSX);
        assertNotNull(source);
    }

    @Test
    void fillMovItensReport() throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/movimentacaoReport.jrxml");
        MovItensViewReportDTO movItensViewReportDTO = new EasyRandom().nextObject(MovItensViewReportDTO.class);
        byte[] source = reportService.fillMovItensReport(List.of(movItensViewReportDTO), jasperReport, new HashMap<>(), TypeDocEnum.XLSX);
        assertNotNull(source);
    }

    @Test
    void fillSitItensReport() throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/itensBySituationReport.jrxml");
        ItemBySituationViewReportDTO situationViewReportDTO = new EasyRandom().nextObject(ItemBySituationViewReportDTO.class);
        byte[] source = reportService.fillSitItensReport(List.of(situationViewReportDTO), jasperReport, new HashMap<>(), TypeDocEnum.TXT);
        assertNotNull(source);
    }

    @Test
    void fillGeneralItensReport() throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/generalItensReport.jrxml");
        GeneralItensReportDTO generalItensReportDTO = new EasyRandom().nextObject(GeneralItensReportDTO.class);
        byte[] source = reportService.fillGeneralItensReport(List.of(generalItensReportDTO), jasperReport, new HashMap<>(), TypeDocEnum.TXT);
        assertNotNull(source);
    }

    @Test
    void fillStationReport() throws JRException, IOException {
        StationDTO stationDTO = new EasyRandom().nextObject(StationDTO.class);
        byte[] source = reportService.fillStationReport(List.of(stationDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void fillContractReport() throws JRException, IOException {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        byte[] source = reportService.fillContractReport(List.of(contractDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void genericReport() throws JRException, IOException {
        GenericReportDTO genericReportDTO = new EasyRandom().nextObject(GenericReportDTO.class);
        byte[] source = reportService.genericReport(List.of(genericReportDTO), new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void fillRegisteredItensReport() throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/registeredItensReport.jrxml");
        RegisteredItensReportDTO registeredItens = new EasyRandom().nextObject(RegisteredItensReportDTO.class);
        byte[] source = reportService.fillRegisteredItensReport(List.of(registeredItens), jasperReport, new HashMap<>(), TypeDocEnum.XLSX);
        assertNotNull(source);
    }

    @Test
    void report() throws JRException, IOException, OutputException, BarcodeException {
        UnityBarcodeDTO unityBarcodeDTO = new UnityBarcodeDTO();
        unityBarcodeDTO.setSituation("Situation");
        unityBarcodeDTO.setId("1");
        unityBarcodeDTO.setDescription("Description");
        CompositionUnityReportDTO compositionUnityReportDTO = CompositionUnityReportDTO.builder()
                .unitiesItem(List.of(unityBarcodeDTO)).unityModel(unityBarcodeDTO).build();
        byte[] source = reportService.report(compositionUnityReportDTO);
        assertNotNull(source);
    }

    @Test
    void fillDepartmentUnityReport() throws JRException, IOException {
        DepartmentUnityDTO departmentUnityDTO = new EasyRandom().nextObject(DepartmentUnityDTO.class);
        byte[] source = reportService.fillDepartmentUnityReport(List.of(departmentUnityDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void fillModelEquipamentReport() throws JRException, IOException {
        ModelEquipTypeDTO modelEquipTypeDTO = new EasyRandom().nextObject(ModelEquipTypeDTO.class);
        byte[] source = reportService.fillModelEquipamentReport(List.of(modelEquipTypeDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void genericReportThreeColumns() throws JRException, IOException {
        GenericReportDTO genericReportDTO = new EasyRandom().nextObject(GenericReportDTO.class);
        byte[] source = reportService.genericReportThreeColumns(List.of(genericReportDTO), new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void fillParameterReport() throws JRException, IOException {
        ParameterDTO parameterDTO = new EasyRandom().nextObject(ParameterDTO.class);
        byte[] source = reportService.fillParameterReport(List.of(parameterDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void forwardTicketReport() throws JRException, IOException {
        ForwardTicketReportDTO forwardTicketReportDTO = new EasyRandom().nextObject(ForwardTicketReportDTO.class);
        byte[] source = reportService.forwardTicketReport(List.of(forwardTicketReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void emitProofReport() throws JRException, IOException {
        EmitProofReportDTO emitProofReportDTO = new EmitProofReportDTO("123", "123", "123", "1230", "123",
        "123", "123", LocalDateTime.now());
        byte[] source = reportService.emitProofReport(List.of(emitProofReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void emitProofProviderReport() throws JRException, IOException {
        EmitProofProviderReportDTO emitProofProviderReportDTO = new EmitProofProviderReportDTO("123", "123", "123", "1230","123", "1230", "123",
                "123", LocalDateTime.now());
        byte[] source = reportService.emitProofProviderReport(List.of(emitProofProviderReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void fillCableMovReport() throws JRException, IOException {
        CableMovementDTO cableMovementDTO = new EasyRandom().nextObject(CableMovementDTO.class);
        byte[] source = reportService.fillCableMovReport(List.of(cableMovementDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void fillCableMovQueryReport() throws JRException, IOException {
        CableMovementDTO cableMovementDTO = new EasyRandom().nextObject(CableMovementDTO.class);
        byte[] source = reportService.fillCableMovQueryReport(List.of(cableMovementDTO),new HashMap<>());
        assertNotNull(source);

    }

    @Test
    void labelReport() throws JRException, IOException {
        BufferedImage image = new BufferedImage(5, 5, 5);
        LabelReportDTO labelReportDTO = LabelReportDTO.builder().barcode1("12345").column1(image).build();
        byte[] source = reportService.labelReport(List.of(labelReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void labelPackingReport() throws JRException, IOException {
        BufferedImage image = new BufferedImage(5, 5, 5);
        LabelReportDTO labelReportDTO = LabelReportDTO.builder().barcode1("12345").column1(image).build();
        byte[] source = reportService.labelPackingReport(List.of(labelReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void trackingRecordReport() throws JRException, IOException {
        BufferedImage image = new BufferedImage(5, 5, 5);
        TrackingRecordDTO trackingRecordDTO = new TrackingRecordDTO("123", "123", "123" ,"123",
                "123", "123", "123" ,"123","123");
        byte[] source = reportService.trackingRecordReport(List.of(trackingRecordDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void summaryBoxReport() throws JRException, IOException {
        UnityBarcodeDTO unityBarcodeDTO = new UnityBarcodeDTO();
        unityBarcodeDTO.setSituation("Situation");
        unityBarcodeDTO.setId("1");
        unityBarcodeDTO.setDescription("Description");
        byte[] source = reportService.summaryBoxReport(List.of(unityBarcodeDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void technicianTicketReport() throws JRException, IOException {
        TechnicianTicketDTO technicianTicketDTO = new TechnicianTicketDTO("123", "123", "123",
                "123", "1233", "1225", "484897", "8948489", "45456",
                "4545", LocalDateTime.now());
        TechnicianTicketReportDTO technicianTicketReportDTO = TechnicianTicketReportDTO.builder().technicianId("1")
                .technicianName("Name").technicianTicket(List.of(technicianTicketDTO)).build();
        byte[] source = reportService.technicianTicketReport(List.of(technicianTicketReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void orderServiceData() throws JRException, IOException {
        OrderServiceDTO orderServiceDTO = new OrderServiceDTO("123", "123", "123",
                "123", "1233", "1225", "484897", "8948489", "45456",
                "4545");
              byte[] source = reportService.orderServiceData(List.of(orderServiceDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void estimateReport() throws JRException, IOException {
        EstimateReportDTO estimateDTO = new EasyRandom().nextObject(EstimateReportDTO.class);
        byte[] source = reportService.estimateReport(List.of(estimateDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void physicalElectricalPropertyReport() throws JRException, IOException {
        PhysicalElectricalPropsReportDTO physicalElectricalPropsReportDTO = new EasyRandom().nextObject(PhysicalElectricalPropsReportDTO.class);
        byte[] source = reportService.physicalElectricalPropertyReport(List.of(physicalElectricalPropsReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void unitExtractionReport() throws JRException, IOException {
        UnitExtractionReportDTO unitExtractionReportDTO = new EasyRandom().nextObject(UnitExtractionReportDTO.class);
        byte[] source = reportService.unitExtractionReport(List.of(unitExtractionReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void userExtractionReport() throws JRException, IOException {
        UserExtractionReportDTO userExtractionReportDTO = new EasyRandom().nextObject(UserExtractionReportDTO.class);
        byte[] source = reportService.userExtractionReport(List.of(userExtractionReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void technicianTimesReport() throws JRException, IOException {
        TechnicianTimesReportDTO technicianTimesReportDTO = new EasyRandom().nextObject(TechnicianTimesReportDTO.class);
        technicianTimesReportDTO.setTotalHours(10);
        technicianTimesReportDTO.setTotalMinutes(10);
        technicianTimesReportDTO.setTotalSeconds(10);
        byte[] source = reportService.technicianTimesReport(List.of(technicianTimesReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void summaryQuantityRepairReport() throws JRException, IOException {
        RepairSummaryReportDTO summaryReportDTO = new EasyRandom().nextObject(RepairSummaryReportDTO.class);
        byte[] source = reportService.summaryQuantityRepairReport(List.of(summaryReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void summaryValueRepairReport() throws JRException, IOException {
        RepairSummaryReportDTO summaryReportDTO = new EasyRandom().nextObject(RepairSummaryReportDTO.class);
        byte[] source = reportService.summaryQuantityRepairReport(List.of(summaryReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void analysisRepairReport() throws JRException, IOException {
        AnalyticRepairReportDTO analyticRepairReportDTO = new EasyRandom().nextObject(AnalyticRepairReportDTO.class);
        byte[] source = reportService.analysisRepairReport(List.of(analyticRepairReportDTO));
        assertNotNull(source);
    }

    @Test
    void operatorTicketReport() throws JRException, IOException {
        OperatorTicketReportDTO operatorTicketReportDTO = new EasyRandom().nextObject(OperatorTicketReportDTO.class);
        byte[] source = reportService.operatorTicketReport(List.of(operatorTicketReportDTO));
        assertNotNull(source);
    }

    @Test
    void ticketReleasedForReturnReport() throws JRException, IOException {
        TicketReleasedDTO ticketReleasedDTO = new EasyRandom().nextObject(TicketReleasedDTO.class);
        byte[] source = reportService.ticketReleasedForReturnReport(List.of(ticketReleasedDTO));
        assertNotNull(source);
    }

    @Test
    void ticketForwardedReport() throws JRException, IOException {
        TicketForwardedReportDTO ticketForwardedReportDTO = new EasyRandom().nextObject(TicketForwardedReportDTO.class);
        byte[] source = reportService.ticketForwardedReport(List.of(ticketForwardedReportDTO));
        assertNotNull(source);
    }

    @Test
    void averageTimeRepairReport() throws JRException, IOException {
        AverageTimeReportDTO averageTimeReportDTO = new EasyRandom().nextObject(AverageTimeReportDTO.class);
        byte[] source = reportService.averageTimeRepairReport(List.of(averageTimeReportDTO));
        assertNotNull(source);
    }

    @Test
    void summaryEquipmentReport() throws JRException, IOException {
        EquipTypeRepairReportDTO equipTypeRepairReportDTO = new EasyRandom().nextObject(EquipTypeRepairReportDTO.class);
        byte[] source = reportService.summaryEquipmentReport(List.of(equipTypeRepairReportDTO));
        assertNotNull(source);
    }

    @Test
    void openRepairReport() throws JRException, IOException {
        OpenRepairReportDTO equipTypeRepairReportDTO = new EasyRandom().nextObject(OpenRepairReportDTO.class);
        byte[] source = reportService.openRepairReport(List.of(equipTypeRepairReportDTO));
        assertNotNull(source);
    }

    @Test
    void costComparisonReport() throws JRException, IOException {
        CostComparisonRepReportDTO costComparisonRepReportDTO = new EasyRandom().nextObject(CostComparisonRepReportDTO.class);
        byte[] source = reportService.costComparisonReport(List.of(costComparisonRepReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void ProductivityComparisonReport() throws JRException, IOException {
        ProductivityComparisonReportDTO productivityComparisonReportDTO = new EasyRandom().nextObject(ProductivityComparisonReportDTO.class);
        byte[] source = reportService.productivityComparisonReport(List.of(productivityComparisonReportDTO),new HashMap<>());
        assertNotNull(source);
    }

    @Test
    void genericQuery() throws JRException, IOException {
        GenericQueryResultDTO genericQueryResultDTO = new EasyRandom().nextObject(GenericQueryResultDTO.class);
        byte[] source = reportService.genericQuery(List.of(genericQueryResultDTO), TypeDocEnum.XLSX);
        assertNotNull(source);
    }

    @Test
    void genericQueryTxt() throws JRException, IOException {
        GenericQueryResultDTO genericQueryResultDTO = new EasyRandom().nextObject(GenericQueryResultDTO.class);
        byte[] source = reportService.genericQuery(List.of(genericQueryResultDTO), TypeDocEnum.TXT);
        assertNotNull(source);
    }

    @Test
    void unityHistoricalReportXLSX() throws JRException, IOException {
        UnityHistoricalViewDTO unityHistoricalViewDTO = new EasyRandom().nextObject(UnityHistoricalViewDTO.class);
        byte[] report = reportService.unityHistoricalReport(List.of(unityHistoricalViewDTO), TypeDocEnum.XLSX);
        assertNotNull(report);
    }

    @Test
    void unityHistoricalReportTXT() throws JRException, IOException {
        UnityHistoricalViewDTO unityHistoricalViewDTO = new EasyRandom().nextObject(UnityHistoricalViewDTO.class);
        byte[] report = reportService.unityHistoricalReport(List.of(unityHistoricalViewDTO), TypeDocEnum.TXT);
        assertNotNull(report);
    }
}
