package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.EstimateNotFoundException;
import br.com.oi.sgis.service.EstimateService;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
class EstimateControllerTest {
    @InjectMocks
    private EstimateController estimateController;

    @Mock
    private EstimateService estimateService;

    @Test
    void listAllWithSearch(){
        List<EstimateDTO> estimateDTOS = new EasyRandom().objects(EstimateDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(estimateDTOS, paging, estimateDTOS.size()));

        Mockito.doReturn(expectedResponse).when(estimateService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<EstimateDTO>> response = estimateController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws EstimateNotFoundException {
        EstimateDTO estimateDTO = new EasyRandom().nextObject(EstimateDTO.class);
        Mockito.doReturn(estimateDTO).when(estimateService).findById(Mockito.any());
        EstimateDTO estimateDTOToReturn = estimateController.findById("1L");

        assertEquals(estimateDTO.getId(), estimateDTOToReturn.getId());
    }

    @Test
    void createEstimate() {
        EstimateDTO estimateDTO = new EasyRandom().nextObject(EstimateDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(estimateService).createEstimate(Mockito.any());
        MessageResponseDTO returnedResponse = estimateController.createEstimate(estimateDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(estimateService).estimateReport(Mockito.any(), Mockito.any(), Mockito.any());
        EstimateDTO estimateDTO = new EasyRandom().nextObject(EstimateDTO.class);
        ResponseEntity<byte[]> responseReport = estimateController.report("", List.of(), List.of());

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void updateEstimate() throws EstimateNotFoundException {
        EstimateDTO estimateDTO = new EasyRandom().nextObject(EstimateDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(estimateService).updateEstimate(Mockito.any());
        MessageResponseDTO returnedResponse = estimateController.updateEstimate(estimateDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void externalOutput() throws EstimateNotFoundException {
        EstimateExternalOutputDTO estimateExternalOutputDTO = new EasyRandom().nextObject(EstimateExternalOutputDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(estimateService).externalOutput(Mockito.any());
        MessageResponseDTO returnedResponse = estimateController.externalOutput(estimateExternalOutputDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void listAllSentWithSearch() {
        List<EstimateDTO> estimateDTOS = new EasyRandom().objects(EstimateDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(estimateDTOS, paging, estimateDTOS.size()));

        Mockito.doReturn(expectedResponse).when(estimateService).listAllSentPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<EstimateDTO>> response = estimateController.listAllSentWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void estimateSentReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(estimateService).estimateSentReport(Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<byte[]> responseReport = estimateController.estimateSentReport("", List.of(), List.of());

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void updateSentEstimate() throws EstimateNotFoundException {
        EstimateDTO estimateDTO = new EasyRandom().nextObject(EstimateDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(estimateService).updateSentEstimate(Mockito.any());
        MessageResponseDTO returnedResponse = estimateController.updateSentEstimate(estimateDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void cancelEstimate() throws EstimateNotFoundException {
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(estimateService).cancelEstimate(Mockito.any());
        MessageResponseDTO returnedResponse = estimateController.cancelEstimate("112233");
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void returnRepair() {
        RepairExternalReturnDTO repairExternalReturnDTO = new EasyRandom().nextObject(RepairExternalReturnDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(estimateService).returnRepair(Mockito.any());
        MessageResponseDTO returnedResponse = estimateController.returnRepair(repairExternalReturnDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void listOpenWithSearch() {
        List<EstimateDTO> estimateDTOS = new EasyRandom().objects(EstimateDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(estimateDTOS, paging, estimateDTOS.size()));

        Mockito.doReturn(expectedResponse).when(estimateService).listAllOpenPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<EstimateDTO>> response = estimateController.listOpenWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }
}