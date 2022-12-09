package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.ModelTechnicianDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.ModelTechnicianNotFoundException;
import br.com.oi.sgis.service.ModelTechnicianService;
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
class ModelTechnicianControllerTest {

    @InjectMocks
    private ModelTechnicianController modelTechnicianController;

    @Mock
    private ModelTechnicianService modelTechnicianService;

    @Test
    void listAllPaginated(){
        List<ModelTechnicianDTO> modelTechnicianDTOS = new EasyRandom().objects(ModelTechnicianDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(modelTechnicianDTOS, paging, modelTechnicianDTOS.size()));

        Mockito.doReturn(expectedResponse).when(modelTechnicianService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ModelTechnicianDTO>> response = modelTechnicianController.listAllPaginated(0, 10,  List.of(), List.of(), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws ModelTechnicianNotFoundException {
        ModelTechnicianDTO modelTechnicianDTO = new EasyRandom().nextObject(ModelTechnicianDTO.class);
        Mockito.doReturn(modelTechnicianDTO).when(modelTechnicianService).findById(Mockito.any());
        ModelTechnicianDTO modelTechnicianDTOToReturn = modelTechnicianController.findById("1L", "1", "1");

        assertEquals(modelTechnicianDTO.getModel().getId(), modelTechnicianDTOToReturn.getModel().getId());
    }

    @Test
    void createModelTechnician() {
        ModelTechnicianDTO modelTechnicianDTO = new EasyRandom().nextObject(ModelTechnicianDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(modelTechnicianService).createModelTechnician(Mockito.any());
        MessageResponseDTO returnedResponse = modelTechnicianController.createModelTechnician(modelTechnicianDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateModelTechnician() throws ModelTechnicianNotFoundException {
        ModelTechnicianDTO modelTechnicianDTO = new EasyRandom().nextObject(ModelTechnicianDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(modelTechnicianService).updateModelTechnician(Mockito.any());
        MessageResponseDTO returnedResponse = modelTechnicianController.updateModelTechnician(modelTechnicianDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(modelTechnicianService).modelTechnicianReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = modelTechnicianController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws ModelTechnicianNotFoundException {
        modelTechnicianController.deleteById("1", "1", "1");
        Mockito.verify(modelTechnicianService, Mockito.times(1)).deleteById(Mockito.any());
    }

}