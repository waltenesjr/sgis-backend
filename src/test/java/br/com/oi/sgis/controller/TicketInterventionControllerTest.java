package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.RepSituationNotFoundException;
import br.com.oi.sgis.exception.TicketInterventionNotFoundException;
import br.com.oi.sgis.service.TicketInterventionService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TicketInterventionControllerTest {

    @InjectMocks
    private TicketInterventionController ticketInterventionController;
    @Mock
    private TicketInterventionService ticketInterventionService;

    @Test
    void listAllPaginated() {
        List<TicketInterventionDTO> ticketInterventionDTOS = new EasyRandom().objects(TicketInterventionDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(ticketInterventionDTOS, paging, ticketInterventionDTOS.size()));

        Mockito.doReturn(expectedResponse).when(ticketInterventionService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<TicketInterventionDTO>> response = ticketInterventionController.listAllPaginated(0, 10,  List.of("sequence"), List.of(), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void openIntervention() {
        TicketInterventionDTO ticketInterventionDTO = new EasyRandom().nextObject(TicketInterventionDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(ticketInterventionService).openIntervention(Mockito.any());
        MessageResponseDTO returnedResponse = ticketInterventionController.openIntervention(ticketInterventionDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void findById() throws TicketInterventionNotFoundException {
        TicketInterventionDTO ticketInterventionDTO = new EasyRandom().nextObject(TicketInterventionDTO.class);
        Mockito.doReturn(ticketInterventionDTO).when(ticketInterventionService).findByIdDTO(Mockito.any());
        TicketInterventionDTO ticketInterventionDTOToReturn = ticketInterventionController.findById(1L, "11");

        assertEquals(ticketInterventionDTO.getSequence(), ticketInterventionDTOToReturn.getSequence());
    }

    @Test
    void technicianReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(ticketInterventionService).technicianReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = ticketInterventionController.technicianReport("1");

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void orderServiceReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(ticketInterventionService).orderServiceReport(Mockito.any(), Mockito.any());
        ResponseEntity<byte[]> responseReport = ticketInterventionController.orderServiceReport(null, null);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void updateInterventionSituation() throws RepSituationNotFoundException {
        SituationDTO situationDTO = new EasyRandom().nextObject(SituationDTO.class);
        Mockito.doReturn(List.of(situationDTO)).when(ticketInterventionService).updateInterventionSituation(Mockito.any());
        List<SituationDTO> response = ticketInterventionController.updateInterventionSituation("1");
        assertEquals(1, response.size());
    }

    @Test
    void updateInterventionSituationEmpty() throws RepSituationNotFoundException {
        Mockito.doReturn(List.of()).when(ticketInterventionService).updateInterventionSituation(Mockito.any());
        List<SituationDTO> response = ticketInterventionController.updateInterventionSituation(Mockito.any());
        assertTrue(response.isEmpty());
    }

    @Test
    void updateIntervention() {
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(ticketInterventionService).updateIntervention(Mockito.any());
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = new EasyRandom().nextObject(TicketInterventionUpdateDTO.class);
        MessageResponseDTO returnedResponse = ticketInterventionController.updateIntervention(ticketInterventionUpdateDTO);
        assertEquals(responseDTO, returnedResponse);
        assertEquals(HttpStatus.OK, returnedResponse.getStatus());
    }

    @Test
    void closeIntervention() {
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(ticketInterventionService).closeIntervention(Mockito.any());
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = new EasyRandom().nextObject(TicketInterventionUpdateDTO.class);
        MessageResponseDTO returnedResponse = ticketInterventionController.closeIntervention(ticketInterventionUpdateDTO);
        assertEquals(responseDTO, returnedResponse);
        assertEquals(HttpStatus.OK, returnedResponse.getStatus());
    }

    @Test
    void listAllToClosePaginated() {
        List<TicketInterventionDTO> ticketInterventionDTOS = new EasyRandom().objects(TicketInterventionDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(ticketInterventionDTOS, paging, ticketInterventionDTOS.size()));

        Mockito.doReturn(expectedResponse).when(ticketInterventionService).listAllToClosePaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<TicketInterventionDTO>> response = ticketInterventionController.listAllToClosePaginated(0, 10,  List.of("sequence"), List.of(), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void listAllToUpdatePaginated() {
        List<TicketInterventionDTO> ticketInterventionDTOS = new EasyRandom().objects(TicketInterventionDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(ticketInterventionDTOS, paging, ticketInterventionDTOS.size()));

        Mockito.doReturn(expectedResponse).when(ticketInterventionService).listAllToUpdatePaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<TicketInterventionDTO>> response = ticketInterventionController.listAllToUpdatePaginated("123",0, 10,  List.of("sequence"), List.of(), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void technicianTimesReport() {
        byte[] report = new byte[50];
        TechnicianTimesFilterDTO filterDTO = new EasyRandom().nextObject(TechnicianTimesFilterDTO.class);
        Mockito.doReturn(report).when(ticketInterventionService).technicianTimesReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = ticketInterventionController.technicianTimesReport(filterDTO);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }
}