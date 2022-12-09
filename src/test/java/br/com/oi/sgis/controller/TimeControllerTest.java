package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.dto.TimeDTO;
import br.com.oi.sgis.exception.TimeNotFoundException;
import br.com.oi.sgis.service.TimeService;
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
class TimeControllerTest {
    @InjectMocks
    private TimeController timeController;

    @Mock
    private TimeService timeService;

    @Test
    void listAllWithSearch(){
        List<TimeDTO> timeDTOS = new EasyRandom().objects(TimeDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(timeDTOS, paging, timeDTOS.size()));

        Mockito.doReturn(expectedResponse).when(timeService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<TimeDTO>> response = timeController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws TimeNotFoundException {
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);
        Mockito.doReturn(timeDTO).when(timeService).findById(Mockito.any());
        TimeDTO timeDTOToReturn = timeController.findById("1L", "2");

        assertEquals(timeDTO.getIntervention().getId(), timeDTOToReturn.getIntervention().getId());
    }

    @Test
    void createTime() {
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(timeService).createTime(Mockito.any());
        MessageResponseDTO returnedResponse = timeController.createTime(timeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateTime() throws TimeNotFoundException {
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(timeService).updateTime(Mockito.any());
        MessageResponseDTO returnedResponse = timeController.updateTime(timeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(timeService).timeReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = timeController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws TimeNotFoundException {
        timeController.deleteById("1", "2");
        Mockito.verify(timeService, Mockito.times(1)).deleteById(Mockito.any());
    }
}