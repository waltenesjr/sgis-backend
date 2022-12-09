package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.MovItensReportDTO;
import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.dto.TechnicalStaffDTO;
import br.com.oi.sgis.enums.MovItensReportOrderEnum;
import br.com.oi.sgis.exception.AreaEquipamentNotFoundException;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.exception.TechnicalStaffNotFoundException;
import br.com.oi.sgis.service.AreaEquipamentService;
import br.com.oi.sgis.service.DepartmentService;
import br.com.oi.sgis.service.SituationService;
import br.com.oi.sgis.service.TechnicalStaffService;
import br.com.oi.sgis.util.MessageUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MovItensReportValidatorTest {

    @InjectMocks
    private MovItensReportValidator movItensReportValidator;

    @Mock
    private AreaEquipamentService areaEquipamentService;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private TechnicalStaffService technicalStaffService;
    @Mock
    private SituationService situationService;

    private MovItensReportDTO movItensReportDTO;

    @BeforeEach
    void setUP(){
        movItensReportDTO = new EasyRandom().nextObject(MovItensReportDTO.class);
        movItensReportDTO.setEndPeriod(LocalDateTime.now());
        movItensReportDTO.setInitialPeriod(LocalDateTime.of(2015,1, 1, 0, 0));
        movItensReportDTO.setFromSituations(null);
        movItensReportDTO.setToSituations(null);
        movItensReportDTO.setOrderBy(MovItensReportOrderEnum.DATA);
    }

    @Test
    void shouldValidate(){
        movItensReportValidator.validate(movItensReportDTO);
    }

    @Test
    void shouldNotValidatePeriod(){
        MovItensReportDTO movItensReportDTO = new EasyRandom().nextObject(MovItensReportDTO.class);
        movItensReportDTO.setInitialPeriod(LocalDateTime.now());
        movItensReportDTO.setEndPeriod(LocalDateTime.of(2015,1, 1, 0, 0));

        Exception e = assertThrows(IllegalArgumentException.class, ()->movItensReportValidator.validate(movItensReportDTO));
        assertEquals("A data inicial não deve ser maior que a data final.", e.getMessage());
    }

    @Test
    void shouldNotValidateUnityCode() throws AreaEquipamentNotFoundException {
        Mockito.doThrow(AreaEquipamentNotFoundException.class).when(areaEquipamentService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->movItensReportValidator.validate(movItensReportDTO));
        assertEquals("Modelo de unidade informado é inválido.", e.getMessage());
    }

    @Test
    void shouldNotValidateFromResponsible() throws DepartmentNotFoundException {
        Mockito.doThrow(DepartmentNotFoundException.class).when(departmentService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->movItensReportValidator.validate(movItensReportDTO));
        assertEquals("Área Origem informada é inválida.", e.getMessage());
    }

    @Test
    void shouldNotValidateToResponsible() throws DepartmentNotFoundException {
        Mockito.when(departmentService.findById(Mockito.any())).thenAnswer(answer->{
            if(answer.getArguments()[0].equals(movItensReportDTO.getToResponsible()))
                throw new DepartmentNotFoundException(MessageUtils.DEPARTMENT_NOT_FOUND_BY_ID.getDescription());
            return DepartmentDTO.builder().id(movItensReportDTO.getFromResponsible()).build();
        });
        Exception e = assertThrows(IllegalArgumentException.class, ()->movItensReportValidator.validate(movItensReportDTO));
        assertEquals("Área Destino informada é inválida.", e.getMessage());
    }

    @Test
    void shouldNotValidgetFromTechinician() throws TechnicalStaffNotFoundException {
        Mockito.doThrow(TechnicalStaffNotFoundException.class).when(technicalStaffService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->movItensReportValidator.validate(movItensReportDTO));
        assertEquals("Técnico Origem informado é inválido.", e.getMessage());
    }

    @Test
    void shouldNotValidgetToTechnician() throws TechnicalStaffNotFoundException {
        Mockito.when(technicalStaffService.findById(Mockito.any())).thenAnswer(answer->{
            if(answer.getArguments()[0].equals(movItensReportDTO.getToTechnician()))
                throw new TechnicalStaffNotFoundException(MessageUtils.TECHNICAL_STAFF_NOT_FOUND_BY_ID.getDescription());
            return TechnicalStaffDTO.builder().id(movItensReportDTO.getFromTechnician()).build();
        });
        Exception e = assertThrows(IllegalArgumentException.class, ()->movItensReportValidator.validate(movItensReportDTO));
        assertEquals("Técnico Destino informado é inválido.", e.getMessage());
    }
    @Test
    void shouldNotValidateFromSituations() {
        movItensReportDTO.setFromSituations(List.of("BXI"));
        List<SituationDTO> situations = List.of(SituationDTO.builder().id("DIS").build(), SituationDTO.builder().id("DEF").build());
        Mockito.doReturn(situations).when(situationService).listAll();
        Exception e = assertThrows(IllegalArgumentException.class, ()->movItensReportValidator.validate(movItensReportDTO));
        assertEquals("Situação Origem informada é inválida.", e.getMessage());
    }

    @Test
    void shouldNotValidateToSituations() {
        movItensReportDTO.setToSituations(List.of("BXI"));
        List<SituationDTO> situations = List.of(SituationDTO.builder().id("DIS").build(), SituationDTO.builder().id("DEF").build());
        Mockito.doReturn(situations).when(situationService).listAll();
        Exception e = assertThrows(IllegalArgumentException.class, ()->movItensReportValidator.validate(movItensReportDTO));
        assertEquals("Situação Destino informada é inválida.", e.getMessage());
    }



}