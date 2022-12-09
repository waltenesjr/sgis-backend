package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.RegisterReasonEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReprocessSapDTOTest {

    @Test
    void reprocessSapDTOTest(){
        ReprocessSapDTO reprocessSapDTO = ReprocessSapDTO.builder().build();

       reprocessSapDTO.setUnityId("1211");
       reprocessSapDTO.setUnityCode("1211");
       reprocessSapDTO.setReservationItem("54564");
       reprocessSapDTO.setReservationNumber("5454");
       reprocessSapDTO.setSerieNumber("241545");
       reprocessSapDTO.setResponsibleId("idResponsible");
       reprocessSapDTO.setOriginUf("MA");
       reprocessSapDTO.setOperation("I");
       reprocessSapDTO.setRegisterReason(RegisterReasonEnum.CUS.getDescription());

        assertEquals("1211", reprocessSapDTO.getUnityId());
        assertEquals("1211", reprocessSapDTO.getUnityCode());
        assertEquals("54564", reprocessSapDTO.getReservationItem());
        assertEquals("5454", reprocessSapDTO.getReservationNumber());
        assertEquals("241545", reprocessSapDTO.getSerieNumber());
        assertEquals("idResponsible", reprocessSapDTO.getResponsibleId());
        assertEquals("MA", reprocessSapDTO.getOriginUf());
        assertEquals("I", reprocessSapDTO.getOperation());
        assertEquals(RegisterReasonEnum.CUS.getDescription(), reprocessSapDTO.getRegisterReason());
        assertNull(reprocessSapDTO.getIdInformaticsRec());
    }


}