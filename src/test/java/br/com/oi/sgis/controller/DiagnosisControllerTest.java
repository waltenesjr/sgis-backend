package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DiagnosisDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.DiagnosisNotFoundException;
import br.com.oi.sgis.service.DiagnosisService;
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
class DiagnosisControllerTest {

    @InjectMocks
    private DiagnosisController diagnosisController;

    @Mock
    private DiagnosisService diagnosisService;

    @Test
    void listAllWithSearch(){
        List<DiagnosisDTO> diagnosisDTOS = new EasyRandom().objects(DiagnosisDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(diagnosisDTOS, paging, diagnosisDTOS.size()));

        Mockito.doReturn(expectedResponse).when(diagnosisService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<DiagnosisDTO>> response = diagnosisController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws DiagnosisNotFoundException {
        DiagnosisDTO diagnosisDTO = new EasyRandom().nextObject(DiagnosisDTO.class);
        Mockito.doReturn(diagnosisDTO).when(diagnosisService).findById(Mockito.any());
        DiagnosisDTO diagnosisDTOToReturn = diagnosisController.findById("1L");

        assertEquals(diagnosisDTO.getId(), diagnosisDTOToReturn.getId());
    }

    @Test
    void createDiagnosis() {
        DiagnosisDTO diagnosisDTO = new EasyRandom().nextObject(DiagnosisDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(diagnosisService).createDiagnosis(Mockito.any());
        MessageResponseDTO returnedResponse = diagnosisController.createDiagnosis(diagnosisDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateDiagnosis() throws DiagnosisNotFoundException {
        DiagnosisDTO diagnosisDTO = new EasyRandom().nextObject(DiagnosisDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(diagnosisService).updateDiagnosis(Mockito.any());
        MessageResponseDTO returnedResponse = diagnosisController.updateDiagnosis(diagnosisDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(diagnosisService).diagnosisReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = diagnosisController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws DiagnosisNotFoundException {
        diagnosisController.deleteById("1");
        Mockito.verify(diagnosisService, Mockito.times(1)).deleteById("1");
    }

}