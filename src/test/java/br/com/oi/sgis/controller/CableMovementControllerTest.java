package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.CableMovementNotFoundException;
import br.com.oi.sgis.service.CableMovementService;
import br.com.oi.sgis.util.PageableUtil;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
class CableMovementControllerTest {

    @InjectMocks
    private CableMovementController cableMovementController;

    @Mock
    private CableMovementService cableMovementService;


    @Test
    void listAll() {
        List<CableMovementDTO> cableMovementDTOS = new EasyRandom().objects(CableMovementDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<CableMovementDTO> expectedResponse = PageableUtil.paginate(new PageImpl(cableMovementDTOS, paging, cableMovementDTOS.size()));
        CableMovementFilterDTO filterDTO = new EasyRandom().nextObject(CableMovementFilterDTO.class);
        Mockito.doReturn(expectedResponse).when(cableMovementService).listAllPaginated(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<CableMovementDTO>> response = cableMovementController.listAll(0, 10,  List.of("id"), List.of("description"), filterDTO);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws CableMovementNotFoundException {
        CableMovementDTO cableMovementDTO = new EasyRandom().nextObject(CableMovementDTO.class);
        Mockito.doReturn(cableMovementDTO).when(cableMovementService).findById(Mockito.any(), Mockito.any());
        CableMovementDTO cableMovementDTOToReturn = cableMovementController.findById("1L", 123L);

        assertEquals(cableMovementDTO.getSequence(), cableMovementDTOToReturn.getSequence());
    }

    @Test
    void createCableMovement() {
        CableMovementDTO cableMovementDTO = new EasyRandom().nextObject(CableMovementDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(cableMovementService).createCableMovement(Mockito.any());
        MessageResponseDTO returnedResponse = cableMovementController.createCableMovement(cableMovementDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        CableMovementFilterDTO filterDTO = new EasyRandom().nextObject(CableMovementFilterDTO.class);

        Mockito.doReturn(report).when(cableMovementService).cableMovementReport(Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<byte[]> responseReport = cableMovementController.report(filterDTO,null, null);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void electricalProp() {
        List<ElectricalPropDTO> propDTOS = new EasyRandom().objects(ElectricalPropDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(propDTOS).when(cableMovementService).listCableMovementUnityProperties(Mockito.any());
        List<ElectricalPropDTO> returnedProps = cableMovementController.electricalProp("123");
        assertEquals(propDTOS.size(), returnedProps.size());
    }

    @Test
    void getCableMovement() {
        List<CableMovementDTO> cableMovementDTOS = new EasyRandom().objects(CableMovementDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<CableMovementDTO> expectedResponse = PageableUtil.paginate(new PageImpl(cableMovementDTOS, paging, cableMovementDTOS.size()));
        CableMovementQueryDTO queryDTO = new EasyRandom().nextObject(CableMovementQueryDTO.class);
        queryDTO.setFinalDate(LocalDateTime.now().plusDays(4));
        queryDTO.setInitialDate(LocalDateTime.now());
        Mockito.doReturn(expectedResponse).when(cableMovementService).getCableMovement(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        PaginateResponseDTO<CableMovementDTO> response = cableMovementController.getCableMovement(queryDTO, 0, 10,  List.of("id"), List.of("description"));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getData().get(0));
    }

    @Test
    void reportQuery() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(cableMovementService).cableMovementQueryReport(Mockito.any(), Mockito.any(), Mockito.any());
        CableMovementQueryDTO queryDTO = new EasyRandom().nextObject(CableMovementQueryDTO.class);
        queryDTO.setFinalDate(LocalDateTime.now().plusDays(4));
        queryDTO.setInitialDate(LocalDateTime.now());
        ResponseEntity<byte[]> responseReport = cableMovementController.reportQuery(queryDTO, null, null);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }
}