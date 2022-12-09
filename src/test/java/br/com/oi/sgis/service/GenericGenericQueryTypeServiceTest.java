package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GenericQueryTypeDTO;
import br.com.oi.sgis.entity.GenericQueryType;
import br.com.oi.sgis.repository.GenericQueryTypeRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GenericGenericQueryTypeServiceTest {

    @InjectMocks
    private GenericQueryTypeService genericQueryTypeService;

    @Mock
    private GenericQueryTypeRepository genericQueryTypeRepository;

    @Test
    void findAll() {
        List<GenericQueryType> genericQueryTypes = new EasyRandom().objects(GenericQueryType.class, 5).collect(Collectors.toList());
        Mockito.doReturn(genericQueryTypes).when(genericQueryTypeRepository).findAll();
        List<GenericQueryTypeDTO> response = genericQueryTypeService.findAll();

        assertFalse(response.isEmpty());
        assertEquals(genericQueryTypes.size(), response.size());
        assertEquals(genericQueryTypes.get(0).getId(), response.get(0).getId());
    }
}