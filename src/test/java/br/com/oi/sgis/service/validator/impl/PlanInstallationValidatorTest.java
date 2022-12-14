package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.PlanInstallationDTO;
import br.com.oi.sgis.dto.StationDTO;
import br.com.oi.sgis.entity.TechnicalStaff;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.exception.TechnicalStaffNotFoundException;
import br.com.oi.sgis.repository.TechnicalStaffRepository;
import br.com.oi.sgis.repository.UnityRepository;
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

import java.util.Optional;

import static br.com.oi.sgis.enums.InstallationReasonEnum.IAC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class PlanInstallationValidatorTest {

    @InjectMocks
    private PlanInstallationValidator planInstallationValidator;
    @Mock
    private UnityRepository unityRepository;
    @Mock
    private StationService stationService;
    @Mock
    private TechnicalStaffRepository technicalStaffRepository;

    @Test @SneakyThrows
    void validate() {
        PlanInstallationDTO planInstallationDTO = new EasyRandom().nextObject(PlanInstallationDTO.class);
        doReturn(Optional.of(Unity.builder().build())).when(unityRepository).findById(Mockito.any());
        doReturn(StationDTO.builder().build()).when(stationService).findById(Mockito.any());
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());
        assertDoesNotThrow(() -> planInstallationValidator.validate(planInstallationDTO));
    }

    @Test
    void invalidateUnity(){
        PlanInstallationDTO planInstallationDTO = new EasyRandom().nextObject(PlanInstallationDTO.class);
        doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class,() -> planInstallationValidator.validate(planInstallationDTO));
        assertEquals("C??digo de unidade inv??lido.", e.getMessage());
    }

    @Test
    void invalidateStation() throws StationNotFoundException {
        PlanInstallationDTO planInstallationDTO = new EasyRandom().nextObject(PlanInstallationDTO.class);
        doReturn(Optional.of(Unity.builder().build())).when(unityRepository).findById(Mockito.any());
        doThrow(StationNotFoundException.class).when(stationService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class,() -> planInstallationValidator.validate(planInstallationDTO));
        assertEquals(MessageUtils.STATION_INVALID.getDescription(), e.getMessage());
    }

    @Test
    void invalidateTechnician() throws TechnicalStaffNotFoundException {
        PlanInstallationDTO planInstallationDTO = new EasyRandom().nextObject(PlanInstallationDTO.class);
        doReturn(Optional.of(Unity.builder().build())).when(unityRepository).findById(Mockito.any());
        doReturn(Optional.empty()).when(technicalStaffRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class,() -> planInstallationValidator.validate(planInstallationDTO));
        assertEquals("T??cnico informado ?? inv??lido.", e.getMessage());
    }

    @Test @SneakyThrows
    void invalidateBONumber() {
        PlanInstallationDTO planInstallationDTO = new EasyRandom().nextObject(PlanInstallationDTO.class);
        planInstallationDTO.setBoNumber(null);
        planInstallationDTO.setInstallationReason(IAC);
        doReturn(Optional.of(Unity.builder().build())).when(unityRepository).findById(Mockito.any());
        doReturn(StationDTO.builder().build()).when(stationService).findById(Mockito.any());
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class,() -> planInstallationValidator.validate(planInstallationDTO));
        assertEquals("O n??mero de BO n??o pode ser nulo para motivos referentes a incidente, inc??ndio ou furtos.", e.getMessage());
    }

    @Test @SneakyThrows
    void invalidateSinisterNumber() {
        PlanInstallationDTO planInstallationDTO = new EasyRandom().nextObject(PlanInstallationDTO.class);
        planInstallationDTO.setSinisterNumber(null);
        planInstallationDTO.setInstallationReason(IAC);
        doReturn(Optional.of(Unity.builder().build())).when(unityRepository).findById(Mockito.any());
        doReturn(StationDTO.builder().build()).when(stationService).findById(Mockito.any());
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class,() -> planInstallationValidator.validate(planInstallationDTO));
        assertEquals("O n??mero de sinistro n??o pode ser nulo para motivos referentes a incidentes ou inc??ndios.", e.getMessage());
    }
}