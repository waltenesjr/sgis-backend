package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;


@ExtendWith(MockitoExtension.class)
class AreaEquipamentDTOTest {

    @Test
    void areaEquipamentTest(){
        LocalDateTime date = LocalDateTime.now();

        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().build();
        areaEquipamentDTO.setDate(date);
        areaEquipamentDTO.setDescription("Teste");
        areaEquipamentDTO.setMnemonic("Minemônico");
        areaEquipamentDTO.setTechniqueCode("123");
        areaEquipamentDTO.setId("001");

        Assertions.assertEquals(date, areaEquipamentDTO.getDate());
        Assertions.assertEquals("Teste", areaEquipamentDTO.getDescription());
        Assertions.assertEquals("Minemônico", areaEquipamentDTO.getMnemonic());
        Assertions.assertEquals("123", areaEquipamentDTO.getTechniqueCode());
        Assertions.assertEquals("001", areaEquipamentDTO.getId());
    }
}