package br.com.oi.sgis.dto;

import br.com.oi.sgis.entity.Uf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StationDTOTest {
    @Test
    void stationDTOTest(){
        StationDTO stationDTO = StationDTO.builder()
                .description("Estacao")
                .id("001")
                .uf(Uf.builder().id("MA").build())
                .address(AddressDTO.builder().id("123").build())
                .build();

        Assertions.assertEquals("Estacao", stationDTO.getDescription());
        Assertions.assertEquals("001", stationDTO.getId());
        Assertions.assertEquals("MA", stationDTO.getUf().getId());
        Assertions.assertEquals("123", stationDTO.getAddress().getId());
    }

}