package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.RecoverItemDTO;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Situation;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.StationService;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecoverItemValidatorTest {

    @InjectMocks
    private RecoverItemValidator recoverItemValidator;

    @Mock
    private StationService stationService;
    @Mock
    private UnityRepository unityRepository;

    @Test
    void validate(){
        RecoverItemDTO recoverItemDTO = RecoverItemDTO.builder().unityId("123").location("123").build();
        Unity unity = Unity.builder().id("testUnity")
                .responsible(Department.builder().id(Utils.getUser().getDepartmentCode().getId()).build())
                .situationCode(Situation.builder().id("BXO").build()).sapStatus("1")
                .build();

        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        assertDoesNotThrow(() -> recoverItemValidator.validate(recoverItemDTO));
    }

    @Test
    void invalidUnitySituation(){
        RecoverItemDTO recoverItemDTO = RecoverItemDTO.builder().unityId("123").location("123").build();
        Unity unity = Unity.builder().id("testUnity")
                .responsible(Department.builder().id(Utils.getUser().getDepartmentCode().getId()).build())
                .situationCode(Situation.builder().id("DIS").build())
                .build();

        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> recoverItemValidator.validate(recoverItemDTO));
        assertEquals(MessageUtils.UNITY_RECOVER_INVALID_SITUATION_ERROR.getDescription() , e.getMessage());
    }
    @Test
    void invalidUnitySAPStatus(){
        RecoverItemDTO recoverItemDTO = RecoverItemDTO.builder().unityId("123").location("123").build();
        Unity unity = Unity.builder().id("testUnity")
                .responsible(Department.builder().id(Utils.getUser().getDepartmentCode().getId()).build())
                .situationCode(Situation.builder().id("BXO").build()).sapStatus(null)
                .build();

        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> recoverItemValidator.validate(recoverItemDTO));
        assertEquals(MessageUtils.UNITY_RECOVER_SAP_STATUS_ERROR.getDescription() , e.getMessage());
    }


    @Test
    void invalidPermission(){
        RecoverItemDTO recoverItemDTO = RecoverItemDTO.builder().unityId("123").location("123").build();
        Unity unity = Unity.builder().id("testUnity")
                .responsible(Department.builder().id("INVALID").build())
                .situationCode(Situation.builder().id("BXO").build()).sapStatus("1")
                .build();

        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> recoverItemValidator.validate(recoverItemDTO));
        assertEquals(MessageUtils.UNITY_DIFFERENT_AREA_ERROR.getDescription() , e.getMessage());
    }

    @Test
    void invalidStation() throws StationNotFoundException {
        RecoverItemDTO recoverItemDTO = RecoverItemDTO.builder().unityId("123").stationId("INVALID").location("123").build();
        Unity unity = Unity.builder().id("testUnity")
                .responsible(Department.builder().id(Utils.getUser().getDepartmentCode().getId()).build())
                .situationCode(Situation.builder().id("BXO").build()).sapStatus("1")
                .build();

        Mockito.doThrow(StationNotFoundException.class).when(stationService).findById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> recoverItemValidator.validate(recoverItemDTO));
        assertEquals(MessageUtils.STATION_INVALID.getDescription() , e.getMessage());
    }
}