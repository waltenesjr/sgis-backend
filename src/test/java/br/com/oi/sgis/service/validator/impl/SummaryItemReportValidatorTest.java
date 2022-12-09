package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.SummaryItemCriteriaReportDTO;
import br.com.oi.sgis.exception.*;
import br.com.oi.sgis.service.*;
import br.com.oi.sgis.util.MessageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SummaryItemReportValidatorTest {

    @InjectMocks
    SummaryItemReportValidator summaryItemReportValidator;

    @Mock
    StationService stationService;
    @Mock
    DepartmentService departmentService;
    @Mock
    CompanyService companyService;
    @Mock
    ApplicationService applicationService;
    @Mock
    AreaEquipamentService areaEquipamentService;
    @Mock
    EquipamentTypeService equipamentTypeService;
    @Mock
    ModelEquipTypeService modelEquipTypeService;

    @Test
    void validate(){
        SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO = SummaryItemCriteriaReportDTO.builder().build();
        assertDoesNotThrow(()->summaryItemReportValidator.validate(summaryItemCriteriaReportDTO));
    }

    @Test
    void invalidStation() throws StationNotFoundException {
        SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO = SummaryItemCriteriaReportDTO.builder().stationCode("station").build();
        Mockito.doThrow(StationNotFoundException.class).when(stationService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->summaryItemReportValidator.validate(summaryItemCriteriaReportDTO));
        assertEquals(MessageUtils.STATION_INVALID.getDescription(), e.getMessage());
    }

    @Test
    void invalidResponsible() throws DepartmentNotFoundException {
        SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO = SummaryItemCriteriaReportDTO.builder().responsibleCode("respo").build();
        Mockito.doThrow(DepartmentNotFoundException.class).when(departmentService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->summaryItemReportValidator.validate(summaryItemCriteriaReportDTO));
        assertEquals(MessageUtils.DEPARTMENT_RESPONSIBLE_INVALID.getDescription(), e.getMessage());
    }

    @Test
    void invalidCompany() throws CompanyNotFoundException {
        SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO = SummaryItemCriteriaReportDTO.builder().companyCode("company").build();
        Mockito.doThrow(CompanyNotFoundException.class).when(companyService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->summaryItemReportValidator.validate(summaryItemCriteriaReportDTO));
        assertEquals("Empresa informada é inválida.", e.getMessage());
    }

    @Test
    void invalidMnemonic() throws AreaEquipamentNotFoundException {
        SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO = SummaryItemCriteriaReportDTO.builder().mnemonic("mnemonic").build();
        Mockito.doThrow(AreaEquipamentNotFoundException.class).when(areaEquipamentService).findByMnemonic(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->summaryItemReportValidator.validate(summaryItemCriteriaReportDTO));
        assertEquals("Mnemonico informado não é válido.", e.getMessage());
    }

    @Test
    void invalidUnity() throws AreaEquipamentNotFoundException {
        SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO = SummaryItemCriteriaReportDTO.builder().unityCode("unity").build();
        Mockito.doThrow(AreaEquipamentNotFoundException.class).when(areaEquipamentService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->summaryItemReportValidator.validate(summaryItemCriteriaReportDTO));
        assertEquals("Modelo de unidade informado é inválido.", e.getMessage());
    }

    @Test
    void invalidModel() throws ModelEquipTypeNotFound {
        SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO = SummaryItemCriteriaReportDTO.builder().modelCode("model").build();
        Mockito.doThrow(ModelEquipTypeNotFound.class).when(modelEquipTypeService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->summaryItemReportValidator.validate(summaryItemCriteriaReportDTO));
        assertEquals("Modelo de equipamento informado é inválido.", e.getMessage());
    }

    @Test
    void invalidType() throws EquipamentTypeNotFoundException {
        SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO = SummaryItemCriteriaReportDTO.builder().typeCode("type").build();
        Mockito.doThrow(EquipamentTypeNotFoundException.class).when(equipamentTypeService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->summaryItemReportValidator.validate(summaryItemCriteriaReportDTO));
        assertEquals("Tipo de equipamento informado é inválido.", e.getMessage());
    }

    @Test
    void invalidApplication() throws ApplicationNotFoundException {
        SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO = SummaryItemCriteriaReportDTO.builder().applicationCode("app").build();
        Mockito.doThrow(ApplicationNotFoundException.class).when(applicationService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->summaryItemReportValidator.validate(summaryItemCriteriaReportDTO));
        assertEquals("Aplicação informada é inválida.", e.getMessage());
    }
}