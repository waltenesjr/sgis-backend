package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.ApplicationDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.ApplicationNotFoundException;
import br.com.oi.sgis.service.ApplicationService;
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
class ApplicationControllerTest {

    @InjectMocks
    private ApplicationController applicationController;

    @Mock
    private ApplicationService applicationService;

    @Test
    void listAllWithSearch(){
        List<ApplicationDTO> applicationDTOS = new EasyRandom().objects(ApplicationDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(applicationDTOS, paging, applicationDTOS.size()));

        Mockito.doReturn(expectedResponse).when(applicationService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ApplicationDTO>> response = applicationController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws ApplicationNotFoundException {
        ApplicationDTO applicationDTO = new EasyRandom().nextObject(ApplicationDTO.class);
        Mockito.doReturn(applicationDTO).when(applicationService).findById(Mockito.any());
        ApplicationDTO applicationDTOToReturn = applicationController.findById("1L");

        assertEquals(applicationDTO.getId(), applicationDTOToReturn.getId());
    }

    @Test
    void createApplication() {
        ApplicationDTO applicationDTO = new EasyRandom().nextObject(ApplicationDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(applicationService).createApplication(Mockito.any());
        MessageResponseDTO returnedResponse = applicationController.createApplication(applicationDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateApplication() throws ApplicationNotFoundException {
        ApplicationDTO applicationDTO = new EasyRandom().nextObject(ApplicationDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(applicationService).updateApplication(Mockito.any());
        MessageResponseDTO returnedResponse = applicationController.updateApplication(applicationDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(applicationService).applicationReport(Mockito.any(), Mockito.any(), Mockito.any());
        ApplicationDTO applicationDTO = new EasyRandom().nextObject(ApplicationDTO.class);
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = applicationController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws ApplicationNotFoundException {
        applicationController.deleteById("1");
        Mockito.verify(applicationService, Mockito.times(1)).deleteById("1");
    }
}