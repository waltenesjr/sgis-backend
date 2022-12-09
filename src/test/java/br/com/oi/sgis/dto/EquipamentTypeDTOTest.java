package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class EquipamentTypeDTOTest {

    @Test
    void equipamentTypeDTOTest(){
        EquipamentTypeDTO equipamentTypeDTO = EquipamentTypeDTO.builder().build();
        equipamentTypeDTO.setId("1233");
        equipamentTypeDTO.setEquipamentName("equipamentType");

        Assertions.assertEquals("1233", equipamentTypeDTO.getId());
        Assertions.assertEquals("equipamentType", equipamentTypeDTO.getEquipamentName());
        Assertions.assertNull(equipamentTypeDTO.getApplication());
        Assertions.assertNull(equipamentTypeDTO.getTechnique());

    }

}