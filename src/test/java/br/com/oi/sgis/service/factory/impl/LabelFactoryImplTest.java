package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.LabelParametersDTO;
import br.com.oi.sgis.dto.LabelReportDTO;
import br.com.oi.sgis.dto.PackingLabelParametersDTO;
import br.com.oi.sgis.dto.UnityDTO;
import br.com.oi.sgis.enums.LabelPrintTypeEnum;
import br.com.oi.sgis.service.UnityService;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class LabelFactoryImplTest {

    @InjectMocks
    private LabelFactoryImpl labelFactory;

    @Mock
    private UnityService unityService;

    @Test
    void generateNextLabelsValues() {
        List<String> nextLabels = new ArrayList<>();
        String lastBarcode = "9016111000200657";
        LabelParametersDTO parametersDTO = LabelParametersDTO.builder().lastEmittedLabel(lastBarcode).labelQuantity(2L)
                .topMargin(0L).lateralMargin(2L).labelHeight(11L).labelWidth(48L)
                .verticalRange(1L).horizontalRange(1L).onlyTest(Boolean.TRUE).build();
        String nextLabel = labelFactory.generateNextLabelsValues(parametersDTO, lastBarcode, nextLabels);
        assertEquals(parametersDTO.getLabelQuantity(), nextLabels.size());
        assertTrue(nextLabels.contains("9016111000200664"));
    }

    @Test
    void generateNextLabelsValuesDuplicate() {
        List<String> nextLabels = new ArrayList<>();
        String lastBarcode = "9016111000200657";
        LabelParametersDTO parametersDTO = LabelParametersDTO.builder().lastEmittedLabel(lastBarcode).labelQuantity(2L)
                .topMargin(0L).lateralMargin(2L).labelHeight(11L).labelWidth(48L).printType(LabelPrintTypeEnum.DUPLICADO)
                .verticalRange(1L).horizontalRange(1L).onlyTest(Boolean.TRUE).build();
        String nextLabel = labelFactory.generateNextLabelsValues(parametersDTO, lastBarcode, nextLabels);
        assertEquals(parametersDTO.getLabelQuantity()*2, nextLabels.size());
        assertTrue(nextLabels.contains("9016111000200664"));
    }

    private void labelsInRows(int labelByRow, int expectedObjectReturn) throws OutputException, BarcodeException, IOException {
        List<String> nextLabels = List.of("9016111000200664", "9016111000200671", "9016111000200688");
        String lastBarcode = "9016111000200657";
        LabelParametersDTO parametersDTO = LabelParametersDTO.builder().lastEmittedLabel(lastBarcode).labelQuantity(3L)
                .topMargin(0L).lateralMargin(2L).labelHeight(11L).labelWidth(48L).labelByLine(labelByRow)
                .verticalRange(1L).horizontalRange(1L).onlyTest(Boolean.TRUE).build();
        List<LabelReportDTO> labels = labelFactory.getLabels(parametersDTO, nextLabels);
        assertEquals(expectedObjectReturn, labels.size());
    }

    @Test
    void getLabels() throws OutputException, BarcodeException, IOException {
        labelsInRows(3, 1);
    }

    @Test
    void getLabelsByLine1Column() throws OutputException, BarcodeException, IOException {
        labelsInRows(1, 3);
    }
    @Test
    void getLabelsByLine2Columns() throws OutputException, BarcodeException, IOException {
        labelsInRows(2, 2);
    }

    private void labelsInColumns(int column) throws OutputException, BarcodeException, IOException {
        List<String> nextLabels = List.of("9016111000200664", "9016111000200671", "9016111000200688");
        String lastBarcode = "9016111000200657";
        LabelParametersDTO parametersDTO = LabelParametersDTO.builder().lastEmittedLabel(lastBarcode).labelQuantity(3L)
                .topMargin(0L).lateralMargin(2L).labelHeight(11L).labelWidth(48L).labelByLine(column).printType(LabelPrintTypeEnum.LINHA)
                .verticalRange(1L).horizontalRange(1L).onlyTest(Boolean.TRUE).build();
        List<LabelReportDTO> labels = labelFactory.getLabels(parametersDTO, nextLabels);
        assertEquals(3, labels.size());
    }

    @Test
    void getLabelsInColumns() throws OutputException, BarcodeException, IOException {
        labelsInColumns(3);
    }

    @Test
    void getLabelsInColumns2() throws OutputException, BarcodeException, IOException {
        labelsInColumns(2);
    }

    @Test
    void getLabelsInColumns1() throws OutputException, BarcodeException, IOException {
        labelsInColumns(1);
    }

    @Test
    void getLabelsPacking() throws OutputException, BarcodeException, IOException {
        List<String> nextLabels = List.of("9016111000200664", "9016111000200671", "9016111000200688");
        PackingLabelParametersDTO parametersDTO = PackingLabelParametersDTO.builder().barcodes(nextLabels).labelQuantity(3L)
                .topMargin(0L).lateralMargin(2L).labelHeight(11L).labelWidth(48L).labelByLine(3)
                .verticalRange(1L).horizontalRange(1L).inhibitBarcode(Boolean.FALSE).build();
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        Mockito.doReturn(unityDTO).when(unityService).findById(Mockito.any());
        List<LabelReportDTO> labels = labelFactory.getLabelsPacking(parametersDTO, nextLabels);
        assertEquals(1, labels.size());
    }

}