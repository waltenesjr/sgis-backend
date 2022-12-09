package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.ActiveClassEnum;
import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import br.com.oi.sgis.enums.RegisterReasonEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ReprocessableUnityDTOTest {
    @Test
    void reprocessableUnityDTOTest(){
        ReprocessableUnityDTO reprocessableUnityDTO = ReprocessableUnityDTO.builder().build();
        reprocessableUnityDTO.setOperation("I");
        reprocessableUnityDTO.setReasonForWriteOff(ReasonForWriteOffEnum.BXP.getReason());
        reprocessableUnityDTO.setRegisterReason(RegisterReasonEnum.CRP.getDescription());
        reprocessableUnityDTO.setUnityId("UnityId");
        reprocessableUnityDTO.setUnityModelId("UnityModelId");
        reprocessableUnityDTO.setSerieNumber("SerieNumber");
        reprocessableUnityDTO.setOrderNumber("OrderNumber");
        reprocessableUnityDTO.setOrderItem("OrderItem");
        reprocessableUnityDTO.setResponsibleId("ResponsibleId");
        reprocessableUnityDTO.setSituation("Situation");
        reprocessableUnityDTO.setOriginUf("MA");
        reprocessableUnityDTO.setAccountantCompany("Company");
        reprocessableUnityDTO.setActiveClass(ActiveClassEnum.ZPFERINS.getCod());

        assertEquals("I", reprocessableUnityDTO.getOperation());
        assertEquals(ReasonForWriteOffEnum.BXP.getReason(), reprocessableUnityDTO.getReasonForWriteOff());
        assertEquals(RegisterReasonEnum.CRP.getDescription(), reprocessableUnityDTO.getRegisterReason());
        assertEquals("UnityId", reprocessableUnityDTO.getUnityId());
        assertEquals("UnityModelId", reprocessableUnityDTO.getUnityModelId());
        assertEquals("SerieNumber", reprocessableUnityDTO.getSerieNumber());
        assertEquals("OrderNumber", reprocessableUnityDTO.getOrderNumber());
        assertEquals("OrderItem", reprocessableUnityDTO.getOrderItem());
        assertEquals("ResponsibleId", reprocessableUnityDTO.getResponsibleId());
        assertEquals("Situation", reprocessableUnityDTO.getSituation());
        assertEquals("MA", reprocessableUnityDTO.getOriginUf());
        assertEquals("Company", reprocessableUnityDTO.getAccountantCompany());
        assertEquals(ActiveClassEnum.ZPFERINS.getCod(), reprocessableUnityDTO.getActiveClass());
    }

}