package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.ComponentNotFoundException;
import br.com.oi.sgis.service.ComponentService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ComponentControllerTest {

    @InjectMocks
    private ComponentController componentController;
    @Mock
    private ComponentService componentService;

    @Test
    void listAllWithSearch() {
        List<ComponentDTO> componentDTOS = new EasyRandom().objects(ComponentDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<ComponentDTO> expectedResponse = PageableUtil.paginate(new PageImpl(componentDTOS, paging, componentDTOS.size()));

        Mockito.doReturn(expectedResponse).when(componentService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ComponentDTO>> response = componentController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws ComponentNotFoundException {
        ComponentDTO componentDTO = new EasyRandom().nextObject(ComponentDTO.class);
        Mockito.doReturn(componentDTO).when(componentService).findById(Mockito.any());
        ComponentDTO componentDTOToReturn = componentController.findById("1L");

        assertEquals(componentDTO.getId(), componentDTOToReturn.getId());
    }

    @Test
    void createComponent() throws ComponentNotFoundException {
        ComponentDTO componentDTO = new EasyRandom().nextObject(ComponentDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(componentService).createComponent(Mockito.any());
        MessageResponseDTO returnedResponse = componentController.createComponent(componentDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateComponent() throws ComponentNotFoundException {
        ComponentDTO componentDTO = new EasyRandom().nextObject(ComponentDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(componentService).updateComponent(Mockito.any());
        MessageResponseDTO returnedResponse = componentController.updateComponent(componentDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void componentTypeList() {
        ComponentTypeDTO componentTypeDTO = new EasyRandom().nextObject(ComponentTypeDTO.class);
        Mockito.doReturn(List.of(componentTypeDTO, componentTypeDTO)).when(componentService).componentTypes();
        List<ComponentTypeDTO> returnComponentTypeList = componentController.componentTypeList();
        assertNotNull(returnComponentTypeList);
        assertEquals(2,returnComponentTypeList.size());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(componentService).componentReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();

        ResponseEntity<byte[]> responseReport = componentController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws ComponentNotFoundException {
        componentController.deleteById("1");
        Mockito.verify(componentService, Mockito.times(1)).deleteById("1");
    }
}