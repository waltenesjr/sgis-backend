package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.ModelEquipTypeDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.ModelEquipTypeNotFound;
import br.com.oi.sgis.service.ModelEquipTypeService;
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
class ModelEquipTypeControllerTest {

    @InjectMocks
    private ModelEquipTypeController modelEquipTypeController;

    @Mock
    private ModelEquipTypeService modelEquipTypeService;

    @Test
    void listAllPaginated() {
        List<ModelEquipTypeDTO> modelEquipTypeDTOS = new EasyRandom().objects(ModelEquipTypeDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(modelEquipTypeDTOS, paging, modelEquipTypeDTOS.size()));

        Mockito.doReturn(expectedResponse).when(modelEquipTypeService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ModelEquipTypeDTO>> response = modelEquipTypeController.listAllPaginated(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }
    @Test
    void findById() throws ModelEquipTypeNotFound {
        ModelEquipTypeDTO modelEquipTypeDTO = new EasyRandom().nextObject(ModelEquipTypeDTO.class);
        Mockito.doReturn(modelEquipTypeDTO).when(modelEquipTypeService).findById(Mockito.any());
        ModelEquipTypeDTO modelEquipTypeDTOToReturn = modelEquipTypeController.findById("1L");

        assertEquals(modelEquipTypeDTO.getId(), modelEquipTypeDTOToReturn.getId());
    }

    @Test
    void createModelEquipType() {
        ModelEquipTypeDTO modelEquipTypeDTO = new EasyRandom().nextObject(ModelEquipTypeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(modelEquipTypeService).createModelEquipType(Mockito.any());
        MessageResponseDTO returnedResponse = modelEquipTypeController.createModelEquipType(modelEquipTypeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateModelEquipType() throws ModelEquipTypeNotFound {
        ModelEquipTypeDTO modelEquipTypeDTO = new EasyRandom().nextObject(ModelEquipTypeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(modelEquipTypeService).updateModelEquipType(Mockito.any());
        MessageResponseDTO returnedResponse = modelEquipTypeController.updateModelEquipType(modelEquipTypeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(modelEquipTypeService).modelEquipTypeReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = modelEquipTypeController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws ModelEquipTypeNotFound{
        modelEquipTypeController.deleteById("1");
        Mockito.verify(modelEquipTypeService, Mockito.times(1)).deleteById("1");
    }

    @Test
    void updateDescontEquip() throws ModelEquipTypeNotFound {
        ModelEquipTypeDTO modelEquipTypeDTO = new EasyRandom().nextObject(ModelEquipTypeDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(modelEquipTypeService).updateDescontEquip(Mockito.any());
        MessageResponseDTO returnedResponse = modelEquipTypeController.updateDescontEquip(modelEquipTypeDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }
}