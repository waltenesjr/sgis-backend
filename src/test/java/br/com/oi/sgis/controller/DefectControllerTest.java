package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DefectDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.DefectNotFoundException;
import br.com.oi.sgis.service.DefectService;
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
class DefectControllerTest {

    @InjectMocks
    private DefectController defectController;

    @Mock
    private DefectService defectService;

    @Test
    void listAll() {
        List<DefectDTO> defects = new EasyRandom().objects(DefectDTO.class, 5).collect(Collectors.toList());

        Mockito.doReturn(defects).when(defectService).listAll();
        List<DefectDTO> defectDTOSToReturn = defectController.listAll();
        assertEquals(defects.size(), defectDTOSToReturn.size());
    }

    @Test
    void listAllWithSearch() {
        List<DefectDTO> defectDTOS = new EasyRandom().objects(DefectDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<DefectDTO> expectedResponse = PageableUtil.paginate(new PageImpl(defectDTOS, paging, defectDTOS.size()));

        Mockito.doReturn(expectedResponse).when(defectService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<DefectDTO>> response = defectController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws DefectNotFoundException {
        DefectDTO defectDTO = new EasyRandom().nextObject(DefectDTO.class);
        Mockito.doReturn(defectDTO).when(defectService).findById(Mockito.any());
        DefectDTO defectDTOToReturn = defectController.findById("1L");

        assertEquals(defectDTO.getId(), defectDTOToReturn.getId());
    }

    @Test
    void createDefect() {
        DefectDTO defectDTO = new EasyRandom().nextObject(DefectDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(defectService).createDefect(Mockito.any());
        MessageResponseDTO returnedResponse = defectController.createDefect(defectDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateDefect() throws DefectNotFoundException {
        DefectDTO defectDTO = new EasyRandom().nextObject(DefectDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(defectService).updateDefect(Mockito.any());
        MessageResponseDTO returnedResponse = defectController.updateDefect(defectDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(defectService).defectReport(Mockito.any(), Mockito.any(), Mockito.any());
        DefectDTO defectDTO = new EasyRandom().nextObject(DefectDTO.class);
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = defectController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws DefectNotFoundException {
        defectController.deleteById("1");
        Mockito.verify(defectService, Mockito.times(1)).deleteById("1");
    }
}