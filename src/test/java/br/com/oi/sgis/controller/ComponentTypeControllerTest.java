package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.ComponentTypeDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.ComponentTypeNotFoundException;
import br.com.oi.sgis.service.ComponentTypeService;
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
class ComponentTypeControllerTest {
    @InjectMocks
    private ComponentTypeController componentTypeController;

    @Mock
    private ComponentTypeService componentTypeService;

    @Test
    void listAllWithSearch(){
        List<ComponentTypeDTO> componentTypeDTOS = new EasyRandom().objects(ComponentTypeDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(componentTypeDTOS, paging, componentTypeDTOS.size()));

        Mockito.doReturn(expectedResponse).when(componentTypeService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ComponentTypeDTO>> response = componentTypeController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws ComponentTypeNotFoundException {
        ComponentTypeDTO componentTypeDTO = new EasyRandom().nextObject(ComponentTypeDTO.class);
        Mockito.doReturn(componentTypeDTO).when(componentTypeService).findById(Mockito.any());
        ComponentTypeDTO componentTypeDTOToReturn = componentTypeController.findById("1L");

        assertEquals(componentTypeDTO.getId(), componentTypeDTOToReturn.getId());
    }

    @Test
    void createComponentType() {
        ComponentTypeDTO componentTypeDTO = new EasyRandom().nextObject(ComponentTypeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(componentTypeService).createComponentType(Mockito.any());
        MessageResponseDTO returnedResponse = componentTypeController.createComponentType(componentTypeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateComponentType() throws ComponentTypeNotFoundException {
        ComponentTypeDTO componentTypeDTO = new EasyRandom().nextObject(ComponentTypeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(componentTypeService).updateComponentType(Mockito.any());
        MessageResponseDTO returnedResponse = componentTypeController.updateComponentType(componentTypeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(componentTypeService).componentTypeReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = componentTypeController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws ComponentTypeNotFoundException {
        componentTypeController.deleteById("1");
        Mockito.verify(componentTypeService, Mockito.times(1)).deleteById("1");
    }
}