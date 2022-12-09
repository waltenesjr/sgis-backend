package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.SituationEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class UnitySituationDTOTest {

    @Test
    void unitySituationDtoTest(){
        UnitySituationDTO unitySituationDTO = UnitySituationDTO.builder().build();
        unitySituationDTO.setSituation(SituationEnum.DIS);
        unitySituationDTO.setUnityId("123");
        unitySituationDTO.setLocation("02.01");

        Assertions.assertEquals(SituationEnum.DIS, unitySituationDTO.getSituation());
        Assertions.assertEquals("123", unitySituationDTO.getUnityId());
        Assertions.assertEquals("02.01", unitySituationDTO.getLocation());
        Assertions.assertNull(unitySituationDTO.getReservationId());
        Assertions.assertNull(unitySituationDTO.getStationId());
    }
}