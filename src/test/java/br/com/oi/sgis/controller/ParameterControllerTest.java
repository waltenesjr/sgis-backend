package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ParameterDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.ParameterNotFoundException;
import br.com.oi.sgis.service.ParameterService;
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
class ParameterControllerTest {
    @InjectMocks
    private ParameterController parameterController;

    @Mock
    private ParameterService parameterService;

    @Test
    void listAllWithSearch(){
        List<ParameterDTO> parameterDTOS = new EasyRandom().objects(ParameterDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(parameterDTOS, paging, parameterDTOS.size()));

        Mockito.doReturn(expectedResponse).when(parameterService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ParameterDTO>> response = parameterController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws ParameterNotFoundException {
        ParameterDTO parameterDTO = new EasyRandom().nextObject(ParameterDTO.class);
        Mockito.doReturn(parameterDTO).when(parameterService).findById(Mockito.any());
        ParameterDTO parameterDTOToReturn = parameterController.findById("1L");

        assertEquals(parameterDTO.getId(), parameterDTOToReturn.getId());
    }

    @Test
    void createParameter() {
        ParameterDTO parameterDTO = new EasyRandom().nextObject(ParameterDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(parameterService).createParameter(Mockito.any());
        MessageResponseDTO returnedResponse = parameterController.createParameter(parameterDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateParameter() throws ParameterNotFoundException {
        ParameterDTO parameterDTO = new EasyRandom().nextObject(ParameterDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(parameterService).updateParameter(Mockito.any());
        MessageResponseDTO returnedResponse = parameterController.updateParameter(parameterDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(parameterService).parameterReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = parameterController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws ParameterNotFoundException {
        parameterController.deleteById("1");
        Mockito.verify(parameterService, Mockito.times(1)).deleteById("1");
    }
}