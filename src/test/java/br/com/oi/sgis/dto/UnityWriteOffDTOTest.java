package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UnityWriteOffDTOTest {

    @Test
    void unityWriteOffDTOTest(){
        UnityWriteOffDTO unityWriteOffDTO = UnityWriteOffDTO.builder().build();
        unityWriteOffDTO.setUnityId("CodUnit");
        unityWriteOffDTO.setReasonForWriteOff(ReasonForWriteOffEnum.OBS);
        unityWriteOffDTO.setSituationID("TRC");
        unityWriteOffDTO.setTechnicalReport("Technical report");

        assertEquals("CodUnit", unityWriteOffDTO.getUnityId());
        assertEquals("TRC", unityWriteOffDTO.getSituationID());
        assertEquals("Technical report", unityWriteOffDTO.getTechnicalReport());
        assertEquals(ReasonForWriteOffEnum.OBS, unityWriteOffDTO.getReasonForWriteOff());
        assertEquals(ReasonForWriteOffEnum.OBS.getReason(), unityWriteOffDTO.getReason());
    }
}