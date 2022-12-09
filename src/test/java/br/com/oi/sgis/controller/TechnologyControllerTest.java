package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.dto.TechnologyDTO;
import br.com.oi.sgis.exception.TechnologyNotFoundException;
import br.com.oi.sgis.service.TechnologyService;
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
class TechnologyControllerTest {
    @InjectMocks
    private TechnologyController technologyController;

    @Mock
    private TechnologyService technologyService;

    @Test
    void listAllWithSearch(){
        List<TechnologyDTO> technologyDTOS = new EasyRandom().objects(TechnologyDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(technologyDTOS, paging, technologyDTOS.size()));

        Mockito.doReturn(expectedResponse).when(technologyService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<TechnologyDTO>> response = technologyController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws TechnologyNotFoundException {
        TechnologyDTO technologyDTO = new EasyRandom().nextObject(TechnologyDTO.class);
        Mockito.doReturn(technologyDTO).when(technologyService).findById(Mockito.any());
        TechnologyDTO technologyDTOToReturn = technologyController.findById("1L");

        assertEquals(technologyDTO.getId(), technologyDTOToReturn.getId());
    }

    @Test
    void createTechnology() {
        TechnologyDTO technologyDTO = new EasyRandom().nextObject(TechnologyDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(technologyService).createTechnology(Mockito.any());
        MessageResponseDTO returnedResponse = technologyController.createTechnology(technologyDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateTechnology() throws TechnologyNotFoundException {
        TechnologyDTO technologyDTO = new EasyRandom().nextObject(TechnologyDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(technologyService).updateTechnology(Mockito.any());
        MessageResponseDTO returnedResponse = technologyController.updateTechnology(technologyDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(technologyService).technologyReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = technologyController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws TechnologyNotFoundException {
        technologyController.deleteById("1");
        Mockito.verify(technologyService, Mockito.times(1)).deleteById("1");
    }
}