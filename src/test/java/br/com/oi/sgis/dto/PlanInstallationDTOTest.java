package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.oi.sgis.enums.InstallationReasonEnum.IAC;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PlanInstallationDTOTest {
    @Test
    void planInstallationDTOTest(){
        PlanInstallationDTO planInstallationDTO = PlanInstallationDTO.builder().build();
        planInstallationDTO.setInstallationReason(IAC);
        planInstallationDTO.setSinisterNumber("12");
        planInstallationDTO.setBoNumber("12");
        planInstallationDTO.setStationId("stationId");
        planInstallationDTO.setTechnicianId("techId");
        planInstallationDTO.setUnityId("unityId");
        planInstallationDTO.setBaBdCode("124");

        assertEquals(IAC, planInstallationDTO.getInstallationReason());
        assertEquals("12", planInstallationDTO.getSinisterNumber());
        assertEquals("12", planInstallationDTO.getBoNumber());
        assertEquals("stationId", planInstallationDTO.getStationId());
        assertEquals("techId", planInstallationDTO.getTechnicianId());
        assertEquals("unityId", planInstallationDTO.getUnityId());
        assertEquals("124", planInstallationDTO.getBaBdCode());
        assertEquals(IAC.getLostReason(), planInstallationDTO.getLostReason());
    }
}