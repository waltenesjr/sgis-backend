package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.EquipamentTypeDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.EquipamentTypeNotFoundException;
import br.com.oi.sgis.service.EquipamentTypeService;
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
class EquipamentTypeControllerTest {
    @InjectMocks
    private EquipamentTypeController equipamentTypeController;

    @Mock
    private EquipamentTypeService equipamentTypeService;

    @Test
    void shouldListAllPaginated(){
        List<EquipamentTypeDTO> equipamentTypeDTOS = new EasyRandom().objects(EquipamentTypeDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(equipamentTypeDTOS, paging, equipamentTypeDTOS.size()));

        Mockito.doReturn(expectedResponse).when(equipamentTypeService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<EquipamentTypeDTO>> response = equipamentTypeController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws EquipamentTypeNotFoundException {
        EquipamentTypeDTO equipamentTypeDTO = new EasyRandom().nextObject(EquipamentTypeDTO.class);
        Mockito.doReturn(equipamentTypeDTO).when(equipamentTypeService).findById(Mockito.any());
        EquipamentTypeDTO equipamentTypeDTOToReturn = equipamentTypeController.findById("1L");

        assertEquals(equipamentTypeDTO.getId(), equipamentTypeDTOToReturn.getId());
    }

    @Test
    void createEquipamentType() {
        EquipamentTypeDTO equipamentTypeDTO = new EasyRandom().nextObject(EquipamentTypeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(equipamentTypeService).createEquipamentType(Mockito.any());
        MessageResponseDTO returnedResponse = equipamentTypeController.createEquipamentType(equipamentTypeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateEquipamentType() throws EquipamentTypeNotFoundException {
        EquipamentTypeDTO equipamentTypeDTO = new EasyRandom().nextObject(EquipamentTypeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(equipamentTypeService).updateEquipamentType(Mockito.any());
        MessageResponseDTO returnedResponse = equipamentTypeController.updateEquipamentType(equipamentTypeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(equipamentTypeService).equipamentTypeReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = equipamentTypeController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws EquipamentTypeNotFoundException {
        equipamentTypeController.deleteById("1");
        Mockito.verify(equipamentTypeService, Mockito.times(1)).deleteById("1");
    }
}