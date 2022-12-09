package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.LabelParametersDTO;
import br.com.oi.sgis.dto.PackingLabelParametersDTO;
import br.com.oi.sgis.dto.PrintLabelTypeDTO;
import br.com.oi.sgis.enums.LabelPrintTypeEnum;
import br.com.oi.sgis.service.LabelService;
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
class LabelControllerTest {
    @InjectMocks
    private LabelController labelController;

    @Mock
    private LabelService labelService;

    @Test
    void typeList() {
        List<PrintLabelTypeDTO> orders = labelController.typeList();
        assertEquals(LabelPrintTypeEnum.values().length,orders.size());
    }

    @Test
    void lastBoxLabel() {
        LabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(LabelParametersDTO.class);
        Mockito.doReturn(labelParametersDTO).when(labelService).getLastBoxLabel();
        LabelParametersDTO lastParametersBox = labelController.lastBoxLabel();
        assertEquals(labelParametersDTO.getLastEmittedLabel(), lastParametersBox.getLastEmittedLabel());
    }

    @Test
    void lastItemLabel() {
        LabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(LabelParametersDTO.class);
        Mockito.doReturn(labelParametersDTO).when(labelService).getLastItemLabel();
        LabelParametersDTO lastParametersItem = labelController.lastItemLabel();
        assertEquals(labelParametersDTO.getLastEmittedLabel(), lastParametersItem.getLastEmittedLabel());
    }

    @Test
    void boxLabel() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(labelService).boxLabels( Mockito.any());
        LabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(LabelParametersDTO.class);
        ResponseEntity<byte[]> responseReport = labelController.boxLabel(labelParametersDTO);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void itemLabel() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(labelService).itemLabels( Mockito.any());
        LabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(LabelParametersDTO.class);
        ResponseEntity<byte[]> responseReport = labelController.itemLabel(labelParametersDTO);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void packingLabel() {
        PackingLabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(PackingLabelParametersDTO.class);
        Mockito.doReturn(labelParametersDTO).when(labelService).getPackingLabel();
        PackingLabelParametersDTO parametersPacking = labelController.getPackingLabel();
        assertEquals(labelParametersDTO.getTopMargin(), parametersPacking.getTopMargin());
    }

    @Test
    void testPackingLabel() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(labelService).packingLabel( Mockito.any());
        PackingLabelParametersDTO labelParametersDTO = new EasyRandom().nextObject(PackingLabelParametersDTO.class);
        ResponseEntity<byte[]> responseReport = labelController.packingLabel(labelParametersDTO);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }
}