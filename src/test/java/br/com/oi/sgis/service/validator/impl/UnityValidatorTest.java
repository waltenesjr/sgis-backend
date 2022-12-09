package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.TechnicalStaffDTO;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.RegisterReasonEnum;
import br.com.oi.sgis.exception.*;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.*;
import br.com.oi.sgis.service.validator.NewSpareUnityValidator;
import br.com.oi.sgis.service.validator.UnityRemovedFromSiteValidator;
import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UnityValidatorTest {

    @Mock
    private UnityRepository unityRepository;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private AreaEquipamentService areaEquipamentService;
    @Mock
    private UnityRemovedFromSiteValidator unityRemovedFromSiteValidator;
    @Mock
    private TechnicalStaffService technicalStaffService;
    @Mock
    private StationService stationService;
    @Mock
    private FiscalDocumentService documentService;
    @Mock
    private NewSpareUnityValidator newSpareUnityValidator;
    @InjectMocks @Spy
    private UnityValidator unityValidator;

    @Test @SneakyThrows
    void shouldValidateNewSpareUnity()  {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        DepartmentDTO responsibleToFind = new EasyRandom().nextObject(DepartmentDTO.class);
        unityToValidate.setRegisterReason(RegisterReasonEnum.CUS.getReason());

        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(responsibleToFind).when(departmentService).findById(Mockito.any());
        Mockito.doNothing().when(newSpareUnityValidator).validateNewSpareUnity(Mockito.any());

        Assertions.assertDoesNotThrow(()->unityValidator.validate(unityToValidate));
        Mockito.verify(departmentService, Mockito.times(2)).findById(Mockito.any());
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
        Mockito.verify(newSpareUnityValidator, Mockito.times(1)).validateNewSpareUnity(Mockito.any());

    }

    @Test @SneakyThrows
    void shouldValidateUnityRemovedFromSite() {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        DepartmentDTO responsibleToFind = new EasyRandom().nextObject(DepartmentDTO.class);
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        unityToValidate.setFiscalDocument(null);
        unityToValidate.setStation(null);
        unityToValidate.setRegisterReason(RegisterReasonEnum.CRP.getReason());

        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(responsibleToFind).when(departmentService).findById(Mockito.any());
        Mockito.doNothing().when(unityRemovedFromSiteValidator).validateUnityRemovedFromSite(Mockito.any());
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());

        Assertions.assertDoesNotThrow(()->unityValidator.validate(unityToValidate));
        Mockito.verify(departmentService, Mockito.times(2)).findById(Mockito.any());
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
        Mockito.verify(unityRemovedFromSiteValidator, Mockito.times(1)).validateUnityRemovedFromSite(Mockito.any());

    }

    @Test
    void shouldThrowExceptionWhenValidateByResevation() throws DepartmentNotFoundException {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);

        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());
        Mockito.doThrow(DepartmentNotFoundException.class).when(departmentService).findById(Mockito.any());

        Assertions.assertThrows(UnityException.class, ()->unityValidator.validate(unityToValidate));
        Mockito.verify(departmentService, Mockito.times(1)).findById(Mockito.any());
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    void shouldThrowExceptionWhenValidateByBarcode() {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);

        Mockito.doReturn(Optional.of(unityToValidate)).when(unityRepository).findById(Mockito.any());

        Assertions.assertThrows(UnityException.class, ()->unityValidator.validate(unityToValidate));
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    void shouldThrowExceptionWhenUnityCodeIsNull() {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        unityToValidate.setUnityCode(null);
        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());


        Assertions.assertThrows(UnityException.class, ()->unityValidator.validate(unityToValidate));
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    void shouldThrowExceptionWhenUnityCodeNotExists() throws AreaEquipamentNotFoundException {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);

        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());
        Mockito.doThrow(AreaEquipamentNotFoundException.class).when(areaEquipamentService).findById(Mockito.any());

        Assertions.assertThrows(UnityException.class, ()->unityValidator.validate(unityToValidate));
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
        Mockito.verify(areaEquipamentService, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    void shouldThrowExceptionWhenTechnicianIsNull() {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        unityToValidate.setTechnician(null);
        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());

        Assertions.assertThrows(UnityException.class, ()->unityValidator.validate(unityToValidate));
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    void shouldThrowExceptionWhenTechnicianNotExists() throws TechnicalStaffNotFoundException {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);

        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());
        Mockito.doThrow(TechnicalStaffNotFoundException.class).when(technicalStaffService).findById(Mockito.any());

        Assertions.assertThrows(UnityException.class, ()->unityValidator.validate(unityToValidate));
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    void shouldThrowExceptionWhenDepositIsNull() {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        unityToValidate.setDeposit(null);
        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());

        Assertions.assertThrows(UnityException.class, ()->unityValidator.validate(unityToValidate));
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    void shouldThrowExceptionWhenDepositNotExists() throws DepartmentNotFoundException {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);
        unityToValidate.setDeposit(Department.builder().id("1").build());
        unityToValidate.setResponsible(Department.builder().id("2").build());

        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(departmentDTO).when(departmentService).findById("2");
        Mockito.doThrow(DepartmentNotFoundException.class).when(departmentService).findById("1");

        Assertions.assertThrows(UnityException.class, ()->unityValidator.validate(unityToValidate));
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    void shouldThrowExceptionWhenStationNotExists() throws StationNotFoundException {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());
        Mockito.doThrow(StationNotFoundException.class).when(stationService).findById(Mockito.any());

        Assertions.assertThrows(UnityException.class, ()->unityValidator.validate(unityToValidate));
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
    }
    @Test
    void shouldThrowExceptionWhenFiscalDocumentNotExists() throws FiscalDocumentNotFoundException {
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.any());
        Mockito.doThrow(FiscalDocumentNotFoundException.class).when(documentService).findById(Mockito.any());

        Assertions.assertThrows(UnityException.class, ()->unityValidator.validate(unityToValidate));
        Mockito.verify(unityRepository, Mockito.times(1)).findById(Mockito.any());
    }
}