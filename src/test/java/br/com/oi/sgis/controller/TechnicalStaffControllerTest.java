package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.TechnicalStaffNotFoundException;
import br.com.oi.sgis.service.TechnicalStaffService;
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
class TechnicalStaffControllerTest {

    @InjectMocks
    TechnicalStaffController technicalStaffController;

    @Mock
    TechnicalStaffService technicalStaffService;

    @Test
    void findById() throws TechnicalStaffNotFoundException {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());
        TechnicalStaffDTO technicalStaffDTOToReturn = technicalStaffController.findById("1L");

        Assertions.assertEquals(technicalStaffDTO.getId(), technicalStaffDTOToReturn.getId());
    }
    @Test
    void listAllPaginated(){
        List<TechnicalStaffDTO> technicalStaffDTOS = new EasyRandom().objects(TechnicalStaffDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(technicalStaffDTOS, paging, technicalStaffDTOS.size()));

        Mockito.doReturn(expectedResponse).when(technicalStaffService).listPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<TechnicalStaffDTO>> response = technicalStaffController.listAllPaginated(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void createTechnicalStaff() {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(technicalStaffService).createTechnicalStaff(Mockito.any());
        MessageResponseDTO returnedResponse = technicalStaffController.createTechnicalStaff(technicalStaffDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateTechnicalStaff() throws TechnicalStaffNotFoundException {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(technicalStaffService).updateTechnicalStaff(Mockito.any());
        MessageResponseDTO returnedResponse = technicalStaffController.updateTechnicalStaff(technicalStaffDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(technicalStaffService).technicalStaffReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = technicalStaffController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws TechnicalStaffNotFoundException {
        technicalStaffController.deleteById("1");
        Mockito.verify(technicalStaffService, Mockito.times(1)).deleteById("1");
    }

    @Test
    void updateManHour() throws TechnicalStaffNotFoundException {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(technicalStaffService).updateManHour(Mockito.any());
        MessageResponseDTO returnedResponse = technicalStaffController.updateManHour(technicalStaffDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void listAllToForwardTicket() {
        List<TechnicalStaffDTO> technicalStaffDTOS = new EasyRandom().objects(TechnicalStaffDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(technicalStaffDTOS, paging, technicalStaffDTOS.size()));

        Mockito.doReturn(expectedResponse).when(technicalStaffService).listAllToForwardTicket(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<TechnicalStaffDTO>> response = technicalStaffController.listAllToForwardTicket(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void transferTechnicalStaff() throws TechnicalStaffNotFoundException {
        TransferTechnicalDTO transferTechnicalDTO = new EasyRandom().nextObject(TransferTechnicalDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(technicalStaffService).transferTechnicalStaff(Mockito.any());
        MessageResponseDTO returnedResponse = technicalStaffController.transferTechnicalStaff(transferTechnicalDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void emitProof() throws JRException, TechnicalStaffNotFoundException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(technicalStaffService).emitProof(Mockito.any());
        EmitProofDTO emitProofDTO = new EasyRandom().nextObject(EmitProofDTO.class);
        ResponseEntity<byte[]> responseReport = technicalStaffController.emitProof(emitProofDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void listAllByUnity() {
        List<TechnicalStaffDTO> technicalStaffDTOS = new EasyRandom().objects(TechnicalStaffDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(technicalStaffDTOS, paging, technicalStaffDTOS.size()));

        Mockito.doReturn(expectedResponse).when(technicalStaffService).listAllByUnity(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<TechnicalStaffDTO>> response = technicalStaffController.listAllByUnity(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }
}