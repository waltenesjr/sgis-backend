package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.util.ReportUtils;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReportService {


    private void addParameters(Map<String, Object> parameters) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        parameters.put("date", LocalDateTime.now().format(formatter));
        addLogoImage(parameters);
    }


    public byte[] report(CompositionUnityReportDTO compositionUnityDTO) throws JRException, IOException, OutputException, BarcodeException {

        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/composicaoItemReport.jrxml");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("unityModel", compositionUnityDTO.getUnityModel().getId());
        parameters.put("descricao", compositionUnityDTO.getUnityModel().getDescription());
        parameters.put("barcodeModel", compositionUnityDTO.getUnityModel().getBarcodeImage());
        addParameters(parameters);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of(compositionUnityDTO), false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);

    }


    public byte[] fillItensBySteal( List<ItensInstallByStealReasonReportDTO> viewReport, JasperReport jasperReport, Map<String, Object> parameters, TypeDocEnum type) throws IOException, JRException {
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportReportByType(type,jasperPrint);
    }

    public byte[] fillStockSummaryReport( List<StockSummaryReportDTO> viewReport, JasperReport jasperReport, Map<String, Object> parameters, TypeDocEnum typeDocEnum) throws IOException, JRException {
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        if(typeDocEnum.getCod().equals(TypeDocEnum.PDF.getCod()))
            return exportPdf(jasperPrint);
        if(typeDocEnum.getCod().equals(TypeDocEnum.TXT.getCod()))
            return exportCsv(jasperPrint);
        return exportXlsx(jasperPrint);
    }

    public byte[] fillSummaryItensReport( List<SummaryItemViewReportDTO> viewReport, JasperReport jasperReport, Map<String, Object> parameters, TypeDocEnum type) throws IOException, JRException {
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportReportByType(type, jasperPrint);
    }

    public byte[] fillMovItensReport( List<MovItensViewReportDTO> viewReport, JasperReport jasperReport, Map<String, Object> parameters, TypeDocEnum type) throws IOException, JRException {
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        return exportReportByType(type, jasperPrint);
    }

    public byte[] fillSitItensReport( List<ItemBySituationViewReportDTO> viewReport, JasperReport jasperReport, Map<String, Object> parameters, TypeDocEnum typeDocEnum) throws IOException, JRException {
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportReportByType(typeDocEnum, jasperPrint);
    }


    private void addLogoImage(Map<String, Object> parameters) throws IOException {
        BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/reports/images/logo.png")));
        parameters.put("logoVital", image);
    }

    private byte[] exportPdf(JasperPrint report) throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        exporter.setExporterInput(new SimpleExporterInput(report));
        exporter.setExporterOutput(
                new SimpleOutputStreamExporterOutput(pdfOutputStream));

        SimplePdfReportConfiguration reportConfig
                = new SimplePdfReportConfiguration();
        reportConfig.setSizePageToContent(true);
        reportConfig.setForceLineBreakPolicy(false);

        exporter.exportReport();
        return pdfOutputStream.toByteArray();

    }

    private byte[] exportXlsx(JasperPrint report) throws JRException {
        JRXlsxExporter exporter = new JRXlsxExporter();
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        final SimpleXlsxReportConfiguration xlsExporterConfiguration = new SimpleXlsxReportConfiguration();
        xlsExporterConfiguration.setIgnoreCellBorder(false);
        xlsExporterConfiguration.setPrintPageLeftMargin(0);
        xlsExporterConfiguration.setPrintPageRightMargin(0);
        xlsExporterConfiguration.setPrintPageTopMargin(0);
        xlsExporterConfiguration.setPrintPageBottomMargin(0);
        xlsExporterConfiguration.setRemoveEmptySpaceBetweenColumns(true);
        xlsExporterConfiguration.setRemoveEmptySpaceBetweenRows(true);
        exporter.setExporterInput(new SimpleExporterInput(report));
        exporter.setExporterOutput(
                new SimpleOutputStreamExporterOutput(pdfOutputStream));

        exporter.setConfiguration(xlsExporterConfiguration);
        exporter.exportReport();
        return pdfOutputStream.toByteArray();

    }

    private byte[] exportCsv(JasperPrint report) throws JRException {
        JRCsvExporter exporter = new JRCsvExporter();
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        final SimpleCsvExporterConfiguration txtConfiguration = new SimpleCsvExporterConfiguration();
        txtConfiguration.setFieldDelimiter(";");
        txtConfiguration.setRecordDelimiter("\n");
        exporter.setConfiguration(txtConfiguration);
        exporter.setExporterInput(new SimpleExporterInput(report));
        exporter.setExporterOutput(
                new SimpleWriterExporterOutput(pdfOutputStream));
        SimplePdfReportConfiguration reportConfig
                = new SimplePdfReportConfiguration();
        reportConfig.setSizePageToContent(true);
        reportConfig.setForceLineBreakPolicy(false);
        exporter.exportReport();
        return pdfOutputStream.toByteArray();

    }

    public byte[] fillGeneralItensReport( List<GeneralItensReportDTO> viewReport, JasperReport jasperReport, Map<String, Object> parameters, TypeDocEnum typeDocEnum) throws IOException, JRException {
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportReportByType(typeDocEnum, jasperPrint);
    }

    private byte[] exportReportByType(TypeDocEnum typeDocEnum, JasperPrint jasperPrint) throws JRException {
        if(typeDocEnum.getCod().equals(TypeDocEnum.PDF.getCod()))
            return exportPdf(jasperPrint);
        if(typeDocEnum.getCod().equals(TypeDocEnum.TXT.getCod()))
            return exportCsv(jasperPrint);
        return exportXlsx(jasperPrint);
    }

    public byte[] genericReport( List<GenericReportDTO> viewReport, Map<String, Object> parameters) throws IOException, JRException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/GenericReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] fillContractReport( List<ContractDTO> viewReport, Map<String, Object> parameters) throws IOException, JRException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/ContractReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] fillStationReport( List<StationDTO> viewReport, Map<String, Object> parameters) throws IOException, JRException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/StationReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] fillParameterReport( List<ParameterDTO> viewReport, Map<String, Object> parameters) throws IOException, JRException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/ParameterReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] fillDepartmentUnityReport( List<DepartmentUnityDTO> viewReport, Map<String, Object> parameters) throws IOException, JRException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/DepartmentUnityReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }
    public byte[] fillModelEquipamentReport( List<ModelEquipTypeDTO> viewReport, Map<String, Object> parameters) throws IOException, JRException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/EquipamentModelReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }


    public byte[] fillRegisteredItensReport(List<RegisteredItensReportDTO> viewReport, JasperReport jasperReport, Map<String, Object> parameters, TypeDocEnum type) throws JRException, IOException {
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(viewReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportReportByType(type,jasperPrint);
    }

    public byte[] genericReportThreeColumns(List<GenericReportDTO> genericReport, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/GenericReportThreeColumns.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(genericReport, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] forwardTicketReport(List<ForwardTicketReportDTO> forwardTickets, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/EncaminhaBrReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(forwardTickets, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] emitProofReport(List<EmitProofReportDTO> emitProofReportDTO, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/ProofTechnicianReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(emitProofReportDTO, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] emitProofProviderReport(List<EmitProofProviderReportDTO> emitProofReportDTO, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/ProofProviderReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(emitProofReportDTO, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] fillCableMovReport(List<CableMovementDTO> cableMovementDTOS, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/MovCable.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(cableMovementDTOS, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] fillCableMovQueryReport(List<CableMovementDTO> cableMovementDTOS, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/MovCableQuery.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(cableMovementDTOS, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] labelReport(List<LabelReportDTO> labels, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/Labels.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(labels, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] labelPackingReport(List<LabelReportDTO> labels, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/LabelsPackingReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(labels, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] trackingRecordReport(List<TrackingRecordDTO> trackingRecordDTO, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/FichaFASReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(trackingRecordDTO, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] summaryBoxReport(List<UnityBarcodeDTO> unitiesBarcode, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/boxSummaryReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(unitiesBarcode, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] technicianTicketReport(List<TechnicianTicketReportDTO> reportData, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/TechnicianTechReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] orderServiceData(List<OrderServiceDTO> orderServiceData, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/OrderServiceReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orderServiceData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] estimateReport(List<EstimateReportDTO> estimateReportDTOS, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/EstimateItemReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(estimateReportDTOS, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] analysisItemEstimateReport(List<ItemEstimatesAnalysisReportDTO> itemEstimateDTOS, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/ItemEstimateAnalysisReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itemEstimateDTOS, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] physicalElectricalPropertyReport(List<PhysicalElectricalPropsReportDTO> reportData, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/PhysicalEPReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] unitExtractionReport(List<UnitExtractionReportDTO> reportData, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/unitExtraction.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportXlsx(jasperPrint);
    }

    public byte[] userExtractionReport(List<UserExtractionReportDTO> usersExtraction, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/userExtraction.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(usersExtraction, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportXlsx(jasperPrint);
    }

    public byte[] technicianTimesReport(List<TechnicianTimesReportDTO> reportData, Map<String, Object> parameters) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/TimesTechnicianReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] summaryQuantityRepairReport(List<RepairSummaryReportDTO> reportData, Map<String, Object> parameters) throws IOException, JRException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/SummaryRepairQuantityReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] summaryValueRepairReport(List<RepairSummaryReportDTO> reportData, Map<String, Object> parameters) throws IOException, JRException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/SummaryRepairValueReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] analysisRepairReport(List<AnalyticRepairReportDTO> reportData) throws IOException, JRException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/AnalysisRepairReport.jrxml");
        Map<String, Object> parameters = new HashMap<>();
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] externalRepairReport(List<ExternalRepairReportDTO> externalRepairReportDTOS) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/ExternalRepairReport.jrxml");
        Map<String, Object> parameters = new HashMap<>();
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(externalRepairReportDTOS, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] operatorTicketReport(List<OperatorTicketReportDTO> reportData) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/OperatorTicketReport.jrxml");
        Map<String, Object> parameters = new HashMap<>();
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] ticketReleasedForReturnReport(List<TicketReleasedDTO> ticketsReleased) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/ReleasedReturnTicketReport.jrxml");
        Map<String, Object> parameters = new HashMap<>();
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ticketsReleased, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] ticketForwardedReport(List<TicketForwardedReportDTO> reportData) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/ForwardedTicketReport.jrxml");
        Map<String, Object> parameters = new HashMap<>();
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] averageTimeRepairReport(List<AverageTimeReportDTO> reportData) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/AverageTimeTicketReport.jrxml");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("totalReport", reportData.size());
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);

    }

    public byte[] summaryEquipmentReport(List<EquipTypeRepairReportDTO> reportData) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/EquipamentTypeRepairReport.jrxml");
        Map<String, Object> parameters = new HashMap<>();
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] openRepairReport(List<OpenRepairReportDTO> reportData) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/OpenTicketReport.jrxml");
        Map<String, Object> parameters = new HashMap<>();
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }
    
    public byte[] costComparisonReport(List<CostComparisonRepReportDTO> equipTypeDTOS, Map<String, Object> parameters) throws IOException, JRException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/CostComparisonReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(equipTypeDTOS, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] productivityComparisonReport(List<ProductivityComparisonReportDTO> reportData,Map<String, Object> parameters ) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/ProductivityComparisonReport.jrxml");
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return exportPdf(jasperPrint);
    }

    public byte[] genericQuery(List<GenericQueryResultDTO> genericQueryResultDTOS, TypeDocEnum type) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/genericQuery.jrxml");
        Map<String, Object> parameters = new HashMap<>();
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(genericQueryResultDTOS, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        if(TypeDocEnum.TXT.getCod().equals(type.getCod()))
            return exportCsv(jasperPrint);
        return exportXlsx(jasperPrint);
    }

    public byte[] unityHistoricalReport(List<UnityHistoricalViewDTO> unityHistoricalDTOs, TypeDocEnum type) throws JRException, IOException {
        JasperReport jasperReport = ReportUtils.getJasperReport("/reports/UnityHistoricalReport.jrxml");
        Map<String, Object> parameters = new HashMap<>();
        addParameters(parameters);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(unityHistoricalDTOs, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        if(TypeDocEnum.TXT.getCod().equals(type.getCod()))
            return exportCsv(jasperPrint);
        return exportXlsx(jasperPrint);
    }
}
