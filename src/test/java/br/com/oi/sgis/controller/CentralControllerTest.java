package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.CentralDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.CentralNotFoundException;
import br.com.oi.sgis.service.CentralService;
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
class CentralControllerTest {

    @InjectMocks
    private CentralController centralController;

    @Mock
    private CentralService centralService;

    @Test
    void listAll() {
        List<CentralDTO> centralDTOS = new EasyRandom().objects(CentralDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<CentralDTO> expectedResponse = PageableUtil.paginate(new PageImpl(centralDTOS, paging, centralDTOS.size()));

        Mockito.doReturn(expectedResponse).when(centralService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<CentralDTO>> response = centralController.listAll(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }


    @Test
    void findById() throws CentralNotFoundException {
        CentralDTO centralDTO = new EasyRandom().nextObject(CentralDTO.class);
        Mockito.doReturn(centralDTO).when(centralService).findById(Mockito.any());
        CentralDTO centralDTOToReturn = centralController.findById("1L");

        assertEquals(centralDTO.getId(), centralDTOToReturn.getId());
    }

    @Test
    void createCentral() {
        CentralDTO centralDTO = new EasyRandom().nextObject(CentralDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(centralService).createCentral(Mockito.any());
        MessageResponseDTO returnedResponse = centralController.createCentral(centralDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateCentral() throws CentralNotFoundException {
        CentralDTO centralDTO = new EasyRandom().nextObject(CentralDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(centralService).updateCentral(Mockito.any());
        MessageResponseDTO returnedResponse = centralController.updateCentral(centralDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(centralService).centralReport(Mockito.any(), Mockito.any(), Mockito.any());
        CentralDTO centralDTO = new EasyRandom().nextObject(CentralDTO.class);
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();

        ResponseEntity<byte[]> responseReport = centralController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws CentralNotFoundException {
        centralController.deleteById("1");
        Mockito.verify(centralService, Mockito.times(1)).deleteById("1");
    }
}