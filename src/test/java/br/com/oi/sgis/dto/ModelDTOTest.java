package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModelDTOTest {

    @Test
    void modelDTOTest(){
        ModelDTO modelDTO = ModelDTO.builder().build();
        modelDTO.setModelCod(1233L);
        modelDTO.setDescription("model");
        modelDTO.setManufacturerCod("123");

        Assertions.assertEquals(1233L, modelDTO.getModelCod());
        Assertions.assertEquals("model", modelDTO.getDescription());
        Assertions.assertEquals("123", modelDTO.getManufacturerCod());

    }

}