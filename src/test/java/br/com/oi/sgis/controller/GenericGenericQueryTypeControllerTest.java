package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.GenericQueryTypeDTO;
import br.com.oi.sgis.service.GenericQueryTypeService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GenericGenericQueryTypeControllerTest {

    @InjectMocks
    private GenericQueryTypeController genericQueryTypeController;

    @Mock
    private GenericQueryTypeService genericQueryTypeService;

    @Test
    void getAll(){
        List<GenericQueryTypeDTO> dto = new EasyRandom().objects(GenericQueryTypeDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(dto).when(genericQueryTypeService).findAll();
        ResponseEntity<List<GenericQueryTypeDTO>> response = genericQueryTypeController.getAll();

        assertNotNull(response.getBody());
        assertEquals(dto.get(0), response.getBody().get(0));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}