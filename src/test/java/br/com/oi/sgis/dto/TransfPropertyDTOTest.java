package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TransfPropertyDTOTest {

    @Test
    void transPropertyDTOTest(){
        TransfPropertyDTO transfPropertyDTO = TransfPropertyDTO.builder().build();
        transfPropertyDTO.setIdDepDestination("123");
        transfPropertyDTO.setIdUnity("14785");
        transfPropertyDTO.setPendency("N");
        transfPropertyDTO.setIdUser("159");

        Assertions.assertEquals("123", transfPropertyDTO.getIdDepDestination());
        Assertions.assertEquals("14785", transfPropertyDTO.getIdUnity());
        Assertions.assertEquals("N", transfPropertyDTO.getPendency());
        Assertions.assertEquals("159", transfPropertyDTO.getIdUser());
    }

}