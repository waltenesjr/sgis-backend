package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ManufacturerDTOTest {

    @Test
    void manufacturerDTOTest(){
        ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder().build();
        manufacturerDTO.setDescription("manufacturer");
        manufacturerDTO.setId("123");

        Assertions.assertEquals("manufacturer", manufacturerDTO.getDescription());
        Assertions.assertEquals("123", manufacturerDTO.getId());
    }

}