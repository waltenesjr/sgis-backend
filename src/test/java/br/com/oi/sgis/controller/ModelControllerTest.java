package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.ModelDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Model;
import br.com.oi.sgis.exception.ModelNotFoundException;
import br.com.oi.sgis.service.ModelService;
import br.com.oi.sgis.util.PageableUtil;
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
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class ModelControllerTest {

    @InjectMocks
    private ModelController modelController;
    @Mock
    private ModelService modelService;

    @Test
    void listAllPaginated(){
        List<Model> models = new EasyRandom().objects(Model.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(models, paging, models.size()));

        Mockito.doReturn(expectedResponse).when(modelService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = modelController.listAllPaginated(0, 10,  List.of("id"), List.of(), "");

        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws ModelNotFoundException {
        ModelDTO modelDTO = new EasyRandom().nextObject(ModelDTO.class);
        Mockito.doReturn(modelDTO).when(modelService).findById(Mockito.any());
        ModelDTO modelDTOToReturn = modelController.findById(modelDTO.getModelCod(),modelDTO.getManufacturerCod());

        assertEquals(modelDTO.getModelCod(),modelDTOToReturn.getModelCod());
    }
}