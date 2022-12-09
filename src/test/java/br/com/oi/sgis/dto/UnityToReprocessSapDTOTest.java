package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.ActiveClassEnum;
import br.com.oi.sgis.enums.RegisterReasonEnum;
import br.com.oi.sgis.enums.SapReprocessConditionEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UnityToReprocessSapDTOTest {
    @Test
    void unityToReprocessSapDTOTest(){
        UnityToReprocessSapDTO unityToReprocess = UnityToReprocessSapDTO.builder().build();
        unityToReprocess.setReason(RegisterReasonEnum.CRP.getReason());
        unityToReprocess.setUnityId("UnityId");
        unityToReprocess.setSerieNumber("SerieNumber");
        unityToReprocess.setOrderNumber("OrderNumber");
        unityToReprocess.setOrderItem("OrderItem");
        unityToReprocess.setResponsible("responsible");
        unityToReprocess.setAccountantCompany("Company");
        unityToReprocess.setActiveClass(ActiveClassEnum.ZPSOBRES.getCod());
        unityToReprocess.setReasonLabel("Motivo de Cadastro");
        unityToReprocess.setUnityCode("UnityCode");
        unityToReprocess.setMnemonic("mnemonic");
        unityToReprocess.setUnityCodeDescription("Description");
        unityToReprocess.setOriginUf("MA");
        unityToReprocess.setCondition(SapReprocessConditionEnum.FIRST.getCondition());

        assertEquals("UnityId", unityToReprocess.getUnityId());
        assertEquals("SerieNumber", unityToReprocess.getSerieNumber());
        assertEquals("OrderNumber", unityToReprocess.getOrderNumber());
        assertEquals("OrderItem", unityToReprocess.getOrderItem());
        assertEquals("responsible", unityToReprocess.getResponsible());
        assertEquals("Company", unityToReprocess.getAccountantCompany());
        assertEquals(ActiveClassEnum.ZPSOBRES.getCod(), unityToReprocess.getActiveClass());
        assertEquals("Motivo de Cadastro", unityToReprocess.getReasonLabel());
        assertEquals("UnityCode", unityToReprocess.getUnityCode());
        assertEquals("mnemonic", unityToReprocess.getMnemonic());
        assertEquals("Description", unityToReprocess.getUnityCodeDescription());
        assertEquals("MA", unityToReprocess.getOriginUf());
        assertEquals(SapReprocessConditionEnum.FIRST.getCondition(), unityToReprocess.getCondition());
        assertNull(unityToReprocess.getFixedNumber());
        assertNull(unityToReprocess.getFixedSubnumber());
        assertNull(unityToReprocess.getReservationItem());
        assertNull(unityToReprocess.getReservationNumber());
    }


}