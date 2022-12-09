package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.BoxNotFoundException;
import br.com.oi.sgis.service.BoxService;
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
class BoxControllerTest {

    @InjectMocks
    private BoxController boxController;

    @Mock
    private BoxService boxService;

    @Test
    void listAllPaginated() {
        List<BoxDTO> boxes = new EasyRandom().objects(BoxDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(boxes, paging, boxes.size()));

        Mockito.doReturn(expectedResponse).when(boxService).listPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = boxController.listAllPaginated(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));

    }

    @Test
    void findById() {
        BoxDTO boxDTO = new EasyRandom().nextObject(BoxDTO.class);
        Mockito.doReturn(boxDTO).when(boxService).findById(Mockito.any());
        BoxDTO boxReturned = boxController.findById("12345");
        Assertions.assertEquals(boxDTO.getId(), boxReturned.getId());
    }

    @Test
    void listBoxType() {
        List<BoxTypeDTO> boxTypeDTOS = new EasyRandom().objects(BoxTypeDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(boxTypeDTOS).when(boxService).listBoxType();

        List<BoxTypeDTO> returned = boxController.listBoxType();
        assertEquals(boxTypeDTOS.size(), returned.size());
    }

    @Test
    void getBoxToUpdate() throws BoxNotFoundException {
        BoxToUpdateDTO boxToUpdateDTO = new EasyRandom().nextObject(BoxToUpdateDTO.class);
        Mockito.doReturn(boxToUpdateDTO).when(boxService).getBoxToUpdate(Mockito.any());
        BoxToUpdateDTO returnedBox = boxController.getBoxToUpdate("1");
        assertEquals(boxToUpdateDTO.getBox().getId(), returnedBox.getBox().getId());
    }

    @Test
    void updateBox() throws BoxNotFoundException {
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).title("Sucesso!").build();
        Mockito.doReturn(messageResponseDTO).when(boxService).updateBox(Mockito.any());
        BoxToUpdateDTO boxToUpdateDTO = new EasyRandom().nextObject(BoxToUpdateDTO.class);

        MessageResponseDTO response = boxController.updateBox(boxToUpdateDTO);
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void summaryBoxReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(boxService).summaryBoxReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = boxController.summaryBoxReport("123");

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }
}