package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ModelEquipTypeDTOTest {
    @Test
    void modelEquipTypeDTOTest(){
        ModelEquipTypeDTO modelEquipTypeDTO = ModelEquipTypeDTO.builder().build();
        modelEquipTypeDTO.setDescription("description");
        modelEquipTypeDTO.setId("123");
        modelEquipTypeDTO.setMnemonic("123");

        assertEquals("description", modelEquipTypeDTO.getDescription());
        assertEquals("123", modelEquipTypeDTO.getId());
        assertEquals("123", modelEquipTypeDTO.getMnemonic());
    }

}