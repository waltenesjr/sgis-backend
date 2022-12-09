package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DollarDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportDollarCrudDTO;
import br.com.oi.sgis.exception.DollarNotFoundException;
import br.com.oi.sgis.service.DollarService;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DollarControllerTest {

    @InjectMocks
    private DollarController dollarController;

    @Mock
    private DollarService dollarService;

    @Test
    void listAllWithSearch(){
        List<DollarDTO> dollarDTOS = new EasyRandom().objects(DollarDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(dollarDTOS, paging, dollarDTOS.size()));

        Mockito.doReturn(expectedResponse).when(dollarService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<DollarDTO>> response = dollarController.listAllWithSearch(0, 10,  List.of("value"), List.of("data"), BigDecimal.ONE, LocalDateTime.now());

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws DollarNotFoundException {
        DollarDTO dollarDTO = new EasyRandom().nextObject(DollarDTO.class);
        Mockito.doReturn(dollarDTO).when(dollarService).findById(Mockito.any());
        DollarDTO dollarDTOToReturn = dollarController.findById(LocalDateTime.now());

        assertEquals(dollarDTO.getDate(), dollarDTOToReturn.getDate());
    }

    @Test
    void createDollar() {
        DollarDTO dollarDTO = new EasyRandom().nextObject(DollarDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(dollarService).createDollar(Mockito.any());
        MessageResponseDTO returnedResponse = dollarController.createDollar(dollarDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateDollar() throws DollarNotFoundException {
        DollarDTO dollarDTO = new EasyRandom().nextObject(DollarDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(dollarService).updateDollar(Mockito.any());
        MessageResponseDTO returnedResponse = dollarController.updateDollar(dollarDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(dollarService).dollarReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        DollarDTO dollarDTO = new EasyRandom().nextObject(DollarDTO.class);
        ReportDollarCrudDTO reportDto = ReportDollarCrudDTO.builder().date(LocalDateTime.now()).value(BigDecimal.ONE)
                .sortAsc(List.of("value")).sortDesc( List.of("data")).build();
        ResponseEntity<byte[]> responseReport = dollarController.report(reportDto );

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws DollarNotFoundException {
        LocalDateTime localDateTime = LocalDateTime.now();
        dollarController.deleteById(localDateTime);
        Mockito.verify(dollarService, Mockito.times(1)).deleteById(localDateTime);
    }


}