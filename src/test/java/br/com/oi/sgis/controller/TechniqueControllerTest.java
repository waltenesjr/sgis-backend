package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.TechniqueDTO;
import br.com.oi.sgis.dto.TechniqueTypeDTO;
import br.com.oi.sgis.enums.TechniqueTypeEnum;
import br.com.oi.sgis.exception.TechniqueNotFoundException;
import br.com.oi.sgis.service.TechniqueService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
class TechniqueControllerTest {
    @InjectMocks
    private TechniqueController techniqueController;

    @Mock
    private TechniqueService techniqueService;

    @Test
    void listAllWithSearch(){
        List<TechniqueDTO> techniqueDTOS = new EasyRandom().objects(TechniqueDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(techniqueDTOS, paging, techniqueDTOS.size()));

        Mockito.doReturn(expectedResponse).when(techniqueService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<TechniqueDTO>> response = techniqueController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws TechniqueNotFoundException {
        TechniqueDTO techniqueDTO = new EasyRandom().nextObject(TechniqueDTO.class);
        Mockito.doReturn(techniqueDTO).when(techniqueService).findById(Mockito.any());
        TechniqueDTO techniqueDTOToReturn = techniqueController.findById("1L");

        assertEquals(techniqueDTO.getId(), techniqueDTOToReturn.getId());
    }

    @Test
    void createTechnique() {
        TechniqueDTO techniqueDTO = new EasyRandom().nextObject(TechniqueDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(techniqueService).createTechnique(Mockito.any());
        MessageResponseDTO returnedResponse = techniqueController.createTechnique(techniqueDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateTechnique() throws TechniqueNotFoundException {
        TechniqueDTO techniqueDTO = new EasyRandom().nextObject(TechniqueDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(techniqueService).updateTechnique(Mockito.any());
        MessageResponseDTO returnedResponse = techniqueController.updateTechnique(techniqueDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }


    @Test
    void deleteById() throws TechniqueNotFoundException {
        techniqueController.deleteById("1");
        Mockito.verify(techniqueService, Mockito.times(1)).deleteById("1");
    }


    @Test
    void techniqueTypes() {
        List<TechniqueTypeDTO> listTechnique = techniqueController.techniqueTypes();
        assertEquals(TechniqueTypeEnum.values().length, listTechnique.size());
    }
}