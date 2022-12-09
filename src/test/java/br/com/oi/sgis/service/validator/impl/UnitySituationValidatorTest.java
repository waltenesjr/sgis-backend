package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.dto.UnitySituationDTO;
import br.com.oi.sgis.entity.Situation;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.DepartmentService;
import br.com.oi.sgis.service.SituationService;
import br.com.oi.sgis.service.StationService;
import br.com.oi.sgis.util.MessageUtils;
import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class UnitySituationValidatorTest {

    @InjectMocks
    private UnitySituationValidator unitySituationValidator;

    @Mock
    private UnityRepository unityRepository;
    @Mock
    private StationService stationService;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private SituationService situationService;

    @Test
    void validate() {

        UnitySituationDTO unitySituationDTO = new EasyRandom().nextObject(UnitySituationDTO.class);
        unitySituationDTO.setSituation(SituationEnum.DIS);
        Unity unity = new EasyRandom().nextObject(Unity.class);

        doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        doReturn(List.of(SituationDTO.builder().id("DIS").build())).when(situationService).listAllToUpdateUnity();

        assertDoesNotThrow(()->unitySituationValidator.validate(unitySituationDTO));
    }

    @Test
    void shouldThrowExceptionSameSituation() {

        UnitySituationDTO unitySituationDTO = new EasyRandom().nextObject(UnitySituationDTO.class);
        unitySituationDTO.setSituation(SituationEnum.DIS);
        Unity unity = new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id(SituationEnum.DIS.getCod()).build());

        doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());

        Exception exception = assertThrows(IllegalArgumentException.class, ()->unitySituationValidator.validate(unitySituationDTO));
        assertEquals("A situação do item já se encontra " + SituationEnum.DIS.getDescription(), exception.getMessage());
    }

    @Test
    void shouldThrowExceptionSituationNotAllowed() {

        UnitySituationDTO unitySituationDTO = new EasyRandom().nextObject(UnitySituationDTO.class);
        unitySituationDTO.setSituation(SituationEnum.EMU);
        Unity unity = new EasyRandom().nextObject(Unity.class);

        doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        doReturn(List.of(SituationDTO.builder().id("DIS").build())).when(situationService).listAllToUpdateUnity();

        Exception e = assertThrows(IllegalArgumentException.class, ()->unitySituationValidator.validate(unitySituationDTO));
        assertEquals("Nova situação diferente de DIS(disponível)/OFE(oferta)/RES(reserva)/DEF(defeituoso).", e.getMessage());
    }

    @Test
    void shouldThrowExceptionSituationNotAllowedNotDIS() {

        List<SituationDTO> alloewdSituation = List.of(SituationDTO.builder().id("OFE").build(), SituationDTO.builder().id("RES").build());
        UnitySituationDTO unitySituationDTO = new EasyRandom().nextObject(UnitySituationDTO.class);
        unitySituationDTO.setSituation(SituationEnum.OFE);
        Unity unity = new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id(SituationEnum.RES.getCod()).description(SituationEnum.RES.getDescription()).build());
        doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        doReturn(alloewdSituation).when(situationService).listAllToUpdateUnity();

        Exception e = assertThrows(IllegalArgumentException.class, ()->unitySituationValidator.validate(unitySituationDTO));
        assertEquals("A situação do item é RESERVA e só pode modificar para DIS.", e.getMessage());
    }

    @Test @SneakyThrows
    void shouldThrowExceptionInvalidStation() {

        UnitySituationDTO unitySituationDTO = new EasyRandom().nextObject(UnitySituationDTO.class);
        unitySituationDTO.setSituation(SituationEnum.DIS);
        Unity unity = new EasyRandom().nextObject(Unity.class);

        doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        doReturn(List.of(SituationDTO.builder().id("DIS").build())).when(situationService).listAllToUpdateUnity();
        doThrow(StationNotFoundException.class).when(stationService).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, ()->unitySituationValidator.validate(unitySituationDTO));
        assertEquals(MessageUtils.STATION_INVALID.getDescription(), e.getMessage());
    }
    @Test @SneakyThrows
    void shouldThrowExceptionInvalidReservation() {

        UnitySituationDTO unitySituationDTO = new EasyRandom().nextObject(UnitySituationDTO.class);
        unitySituationDTO.setSituation(SituationEnum.RES);
        Unity unity = new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id(SituationEnum.DIS.getCod()).description(SituationEnum.DIS.getDescription()).build());

        doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        doReturn(List.of(SituationDTO.builder().id("RES").build())).when(situationService).listAllToUpdateUnity();
        doThrow(DepartmentNotFoundException.class).when(departmentService).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, ()->unitySituationValidator.validate(unitySituationDTO));
        assertEquals(MessageUtils.UNITY_SITUATION_INVALID_RESERVATION.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void shouldThrowExceptionNullReservation() {

        UnitySituationDTO unitySituationDTO = new EasyRandom().nextObject(UnitySituationDTO.class);
        unitySituationDTO.setSituation(SituationEnum.RES);
        unitySituationDTO.setReservationId(null);
        Unity unity = new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id(SituationEnum.DIS.getCod()).description(SituationEnum.DIS.getDescription()).build());

        doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        doReturn(List.of(SituationDTO.builder().id("RES").build())).when(situationService).listAllToUpdateUnity();

        Exception e = assertThrows(IllegalArgumentException.class, ()->unitySituationValidator.validate(unitySituationDTO));
        assertEquals("A área reserva não deve ser nula para situação Reserva.", e.getMessage());
    }

    @Test @SneakyThrows
    void shouldThrowExceptionReservation() {

        UnitySituationDTO unitySituationDTO = new EasyRandom().nextObject(UnitySituationDTO.class);
        unitySituationDTO.setSituation(SituationEnum.RES);
        Unity unity = new EasyRandom().nextObject(Unity.class);

        doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        doReturn(List.of(SituationDTO.builder().id("RES").build())).when(situationService).listAllToUpdateUnity();

        Exception e = assertThrows(IllegalArgumentException.class, ()->unitySituationValidator.validate(unitySituationDTO));
        assertEquals("O item para ser reservado tem que estar disponível.", e.getMessage());
    }

}