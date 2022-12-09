package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.LabelParametersDTO;
import br.com.oi.sgis.dto.PackingLabelParametersDTO;
import br.com.oi.sgis.dto.TechnicalStaffDTO;
import br.com.oi.sgis.exception.TechnicalStaffNotFoundException;
import br.com.oi.sgis.repository.ParameterRepository;
import br.com.oi.sgis.service.factory.LabelFactory;
import br.com.oi.sgis.util.MessageUtils;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)

class LabelServiceTest {

    @InjectMocks
    private LabelService labelService;

    @Mock
    private TechnicalStaffService technicalStaffService;

    @Mock
    private ReportService reportService;
    @Mock
    private LabelFactory labelFactory;
    @Mock
    private ParameterRepository parameterRepository;

    @Test
    void getLastBoxLabel() throws TechnicalStaffNotFoundException {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.getCgcCpfCompany().setBoxCode("1234567891234556");
        technicalStaffDTO.getCgcCpfCompany().setPrefix(technicalStaffDTO.getCgcCpfCompany().getBoxCode().substring(2,4));
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        LabelParametersDTO lastLabelBox = labelService.getLastBoxLabel();
        assertEquals(lastLabelBox.getLastEmittedLabel(), technicalStaffDTO.getCgcCpfCompany().getBoxCode());
    }

    @Test
    void getLastItemLabel() throws TechnicalStaffNotFoundException {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.getCgcCpfCompany().setBarcode("1234567891234556");
        technicalStaffDTO.getCgcCpfCompany().setPrefix(technicalStaffDTO.getCgcCpfCompany().getBarcode().substring(2,4));
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        LabelParametersDTO lastLabelBox = labelService.getLastItemLabel();
        assertEquals(lastLabelBox.getLastEmittedLabel(), technicalStaffDTO.getCgcCpfCompany().getBarcode());
    }

    @Test
    void getLastItemLabelExceptionSize() throws TechnicalStaffNotFoundException {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.getCgcCpfCompany().setBarcode("123456789");
        technicalStaffDTO.getCgcCpfCompany().setPrefix(technicalStaffDTO.getCgcCpfCompany().getBarcode().substring(2,4));
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()-> labelService.getLastItemLabel());
        assertEquals(MessageUtils.LABEL_SIZE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void getLastItemLabelExceptionHolding() throws TechnicalStaffNotFoundException {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.getCgcCpfCompany().setBarcode("1234567891234556");
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()-> labelService.getLastItemLabel());
        assertEquals(MessageUtils.LABEL_HOLDING_ERROR.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void boxLabels() {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.getCgcCpfCompany().setBarcode("1234567891234556");
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        LabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(LabelParametersDTO.class);
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).labelReport(Mockito.any(), Mockito.any());
        byte[] returnedReport = labelService.boxLabels(labelParametersDTO);
        assertNotNull(returnedReport);

    }

    @Test @SneakyThrows
    void boxLabelsException() {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.getCgcCpfCompany().setBarcode("1234567891234556");
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        LabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(LabelParametersDTO.class);
        Mockito.doThrow(JRException.class).when(reportService).labelReport(Mockito.any(), Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> labelService.boxLabels(labelParametersDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());

    }

    @Test @SneakyThrows
    void boxLabelsNotTest() {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.getCgcCpfCompany().setBarcode("1234567891234556");
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        LabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(LabelParametersDTO.class);
        labelParametersDTO.setOnlyTest(Boolean.FALSE);
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).labelReport(Mockito.any(), Mockito.any());
        byte[] returnedReport = labelService.boxLabels(labelParametersDTO);
        assertNotNull(returnedReport);
        Mockito.verify(parameterRepository, Mockito.times(1)).updateBoxCode(Mockito.any(), Mockito.any());

    }

    @Test @SneakyThrows
    void itemLabels() {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.getCgcCpfCompany().setBarcode("1234567891234556");
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        LabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(LabelParametersDTO.class);
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).labelReport(Mockito.any(), Mockito.any());
        byte[] returnedReport = labelService.itemLabels(labelParametersDTO);
        assertNotNull(returnedReport);
    }

    @Test @SneakyThrows
    void itemLabelsException() {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.getCgcCpfCompany().setBarcode("1234567891234556");
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        LabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(LabelParametersDTO.class);
        Mockito.doThrow(JRException.class).when(reportService).labelReport(Mockito.any(), Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> labelService.itemLabels(labelParametersDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void itemLabelsNotTest() {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.getCgcCpfCompany().setBarcode("1234567891234556");
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        LabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(LabelParametersDTO.class);
        labelParametersDTO.setOnlyTest(Boolean.FALSE);
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).labelReport(Mockito.any(), Mockito.any());
        byte[] returnedReport = labelService.itemLabels(labelParametersDTO);
        assertNotNull(returnedReport);
        Mockito.verify(parameterRepository, Mockito.times(1)).updateBarcode(Mockito.any(), Mockito.any());
    }

    @Test
    void getPackingLabel() throws TechnicalStaffNotFoundException {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.getCgcCpfCompany().setBarcode("1234567891234556");
        technicalStaffDTO.getCgcCpfCompany().setPrefix(technicalStaffDTO.getCgcCpfCompany().getBarcode().substring(2,4));
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        PackingLabelParametersDTO lastLabelBox = labelService.getPackingLabel();
        assertEquals(5L, lastLabelBox.getTopMargin());
    }

    @Test @SneakyThrows
    void packingLabel() {

        PackingLabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(PackingLabelParametersDTO.class);
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).labelPackingReport(Mockito.any(), Mockito.any());
        byte[] returnedReport = labelService.packingLabel(labelParametersDTO);
        assertNotNull(returnedReport);
    }

    @Test
    void packingLabelException() throws JRException, IOException {
        PackingLabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(PackingLabelParametersDTO.class);
        Mockito.doThrow(JRException.class).when(reportService).labelPackingReport(Mockito.any(), Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> labelService.packingLabel(labelParametersDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }
}