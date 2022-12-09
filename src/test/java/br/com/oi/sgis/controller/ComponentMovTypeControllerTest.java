package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.MovTypeEnum;
import br.com.oi.sgis.enums.SignalMovTypeEnum;
import br.com.oi.sgis.exception.ComponentMovTypeNotFoundException;
import br.com.oi.sgis.service.ComponentMovTypeService;
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
class ComponentMovTypeControllerTest {
    @InjectMocks
    private ComponentMovTypeController componentMovTypeController;

    @Mock
    private ComponentMovTypeService componentMovTypeService;

    @Test
    void listAllWithSearch(){
        List<ComponentMovTypeDTO> componentMovTypeDTOS = new EasyRandom().objects(ComponentMovTypeDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(componentMovTypeDTOS, paging, componentMovTypeDTOS.size()));

        Mockito.doReturn(expectedResponse).when(componentMovTypeService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ComponentMovTypeDTO>> response = componentMovTypeController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws ComponentMovTypeNotFoundException {
        ComponentMovTypeDTO componentMovTypeDTO = new EasyRandom().nextObject(ComponentMovTypeDTO.class);
        Mockito.doReturn(componentMovTypeDTO).when(componentMovTypeService).findById(Mockito.any());
        ComponentMovTypeDTO componentMovTypeDTOToReturn = componentMovTypeController.findById("1L");

        assertEquals(componentMovTypeDTO.getId(), componentMovTypeDTOToReturn.getId());
    }

    @Test
    void createComponentMovType() {
        ComponentMovTypeDTO componentMovTypeDTO = new EasyRandom().nextObject(ComponentMovTypeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(componentMovTypeService).createComponentMovType(Mockito.any());
        MessageResponseDTO returnedResponse = componentMovTypeController.createComponentMovType(componentMovTypeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateComponentMovType() throws ComponentMovTypeNotFoundException {
        ComponentMovTypeDTO componentMovTypeDTO = new EasyRandom().nextObject(ComponentMovTypeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(componentMovTypeService).updateComponentMovType(Mockito.any());
        MessageResponseDTO returnedResponse = componentMovTypeController.updateComponentMovType(componentMovTypeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(componentMovTypeService).componentMovTypeReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = componentMovTypeController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws ComponentMovTypeNotFoundException {
        componentMovTypeController.deleteById("1");
        Mockito.verify(componentMovTypeService, Mockito.times(1)).deleteById("1");
    }

    @Test
    void signalList() {
        List<SignalMovTypeDTO> signalMovTypeDTOS = componentMovTypeController.signalList();
        assertEquals(SignalMovTypeEnum.values().length, signalMovTypeDTOS.size());
    }

    @Test
    void typeList() {
        List<MovTypeDTO> movTypeDTOS = componentMovTypeController.typeList();
        assertEquals(MovTypeEnum.values().length, movTypeDTOS.size());
    }
}