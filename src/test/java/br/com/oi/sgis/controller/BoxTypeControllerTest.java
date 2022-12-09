package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.BoxTypeDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.BoxTypeNotFoundException;
import br.com.oi.sgis.service.BoxTypeService;
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
class BoxTypeControllerTest {
    @InjectMocks
    private BoxTypeController boxTypeController;

    @Mock
    private BoxTypeService boxTypeService;

    @Test
    void listAllWithSearch(){
        List<BoxTypeDTO> boxTypeDTOS = new EasyRandom().objects(BoxTypeDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(boxTypeDTOS, paging, boxTypeDTOS.size()));

        Mockito.doReturn(expectedResponse).when(boxTypeService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<BoxTypeDTO>> response = boxTypeController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws BoxTypeNotFoundException {
        BoxTypeDTO boxTypeDTO = new EasyRandom().nextObject(BoxTypeDTO.class);
        Mockito.doReturn(boxTypeDTO).when(boxTypeService).findById(Mockito.any());
        BoxTypeDTO boxTypeDTOToReturn = boxTypeController.findById("1L");

        assertEquals(boxTypeDTO.getId(), boxTypeDTOToReturn.getId());
    }

    @Test
    void createBoxType() {
        BoxTypeDTO boxTypeDTO = new EasyRandom().nextObject(BoxTypeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(boxTypeService).createBoxType(Mockito.any());
        MessageResponseDTO returnedResponse = boxTypeController.createBoxType(boxTypeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateBoxType() throws BoxTypeNotFoundException {
        BoxTypeDTO boxTypeDTO = new EasyRandom().nextObject(BoxTypeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(boxTypeService).updateBoxType(Mockito.any());
        MessageResponseDTO returnedResponse = boxTypeController.updateBoxType(boxTypeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(boxTypeService).boxTypeReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = boxTypeController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws BoxTypeNotFoundException {
        boxTypeController.deleteById("1");
        Mockito.verify(boxTypeService, Mockito.times(1)).deleteById("1");
    }
}
