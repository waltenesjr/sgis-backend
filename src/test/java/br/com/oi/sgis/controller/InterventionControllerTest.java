package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.InterventionDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.exception.InterventionNotFoundException;
import br.com.oi.sgis.service.InterventionService;
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
class InterventionControllerTest {
    @InjectMocks
    private InterventionController interventionController;

    @Mock
    private InterventionService interventionService;

    @Test
    void listAllWithSearch(){
        List<InterventionDTO> interventionDTOS = new EasyRandom().objects(InterventionDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<InterventionDTO> expectedResponse = PageableUtil.paginate(new PageImpl(interventionDTOS, paging, interventionDTOS.size()));

        Mockito.doReturn(expectedResponse).when(interventionService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<InterventionDTO>> response = interventionController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws InterventionNotFoundException {
        InterventionDTO interventionDTO = new EasyRandom().nextObject(InterventionDTO.class);
        Mockito.doReturn(interventionDTO).when(interventionService).findById(Mockito.any());
        InterventionDTO interventionDTOToReturn = interventionController.findById("1L");

        assertEquals(interventionDTO.getId(), interventionDTOToReturn.getId());
    }

    @Test
    void createIntervention() {
        InterventionDTO interventionDTO = new EasyRandom().nextObject(InterventionDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(interventionService).createIntervention(Mockito.any());
        MessageResponseDTO returnedResponse = interventionController.createIntervention(interventionDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateIntervention() throws InterventionNotFoundException {
        InterventionDTO interventionDTO = new EasyRandom().nextObject(InterventionDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(interventionService).updateIntervention(Mockito.any());
        MessageResponseDTO returnedResponse = interventionController.updateIntervention(interventionDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(interventionService).interventionReport(Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<byte[]> responseReport = interventionController.report("", List.of(), List.of());

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws InterventionNotFoundException {
        interventionController.deleteById("1");
        Mockito.verify(interventionService, Mockito.times(1)).deleteById("1");
    }
}