package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.dto.StationDTO;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.service.StationService;
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
class StationControllerTest {

    @InjectMocks
    private StationController stationController;

    @Mock
    private StationService stationService;

    @Test
    void findById() throws StationNotFoundException {
        StationDTO stationDTO = new EasyRandom().nextObject(StationDTO.class);
        Mockito.doReturn(stationDTO).when(stationService).findById(Mockito.any());
        StationDTO stationDTOToReturn = stationController.findById("1L");

        Assertions.assertEquals(stationDTO.getId(), stationDTOToReturn.getId());
    }

    @Test
    void listAllPaginated(){
        List<StationDTO> stationDTOS = new EasyRandom().objects(StationDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(stationDTOS, paging, stationDTOS.size()));

        Mockito.doReturn(expectedResponse).when(stationService).listPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<StationDTO>> response = stationController.listAllPaginated(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void createStation() {
        StationDTO stationDTO = new EasyRandom().nextObject(StationDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(stationService).createStation(Mockito.any());
        MessageResponseDTO returnedResponse = stationController.createStation(stationDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateStation() throws StationNotFoundException {
        StationDTO stationDTO = new EasyRandom().nextObject(StationDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(stationService).updateStation(Mockito.any());
        MessageResponseDTO returnedResponse = stationController.updateStation(stationDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(stationService).stationReport(Mockito.any(), Mockito.any(), Mockito.any());
        StationDTO stationDTO = new EasyRandom().nextObject(StationDTO.class);
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = stationController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws StationNotFoundException {
        stationController.deleteById("1");
        Mockito.verify(stationService, Mockito.times(1)).deleteById("1");
    }

}