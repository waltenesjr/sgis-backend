package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class UnitySwapDTOTest {

    @Test
    void unitSwapDtoTest(){
        UnitySwapDTO unitySwapDTO = UnitySwapDTO.builder().build();
        unitySwapDTO.setUnityNewBarcode("1235");
        unitySwapDTO.setUnityId("54125");
        unitySwapDTO.setNewUnityCode("15475");
        unitySwapDTO.setNewSerieNumber("147");

        Assertions.assertEquals("1235", unitySwapDTO.getUnityNewBarcode());
        Assertions.assertEquals("54125", unitySwapDTO.getUnityId());
        Assertions.assertEquals("15475", unitySwapDTO.getNewUnityCode());
        Assertions.assertEquals("147", unitySwapDTO.getNewSerieNumber());
    }

}