package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.ItemBySitReportCriteriaDTO;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.service.DepartmentService;
import br.com.oi.sgis.util.MessageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemBySitReportValidatorTest {

    @InjectMocks
    private ItemBySitReportValidator itemBySitReportValidator;
    @Mock
    DepartmentService departmentService;

    @Test
    void validate() {
        ItemBySitReportCriteriaDTO criteriaDTO = ItemBySitReportCriteriaDTO.builder().build();
        assertDoesNotThrow(()->itemBySitReportValidator.validate(criteriaDTO));
    }

    @Test
    void invalidateDepartment() throws DepartmentNotFoundException {
        ItemBySitReportCriteriaDTO criteriaDTO = ItemBySitReportCriteriaDTO.builder().department("dep").build();
        Mockito.doThrow(DepartmentNotFoundException.class).when(departmentService).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->itemBySitReportValidator.validate(criteriaDTO));
        assertEquals(MessageUtils.DEPARTMENT_INVALID.getDescription(), e.getMessage());
    }

    @Test
    void invalidateRegisterPeriod(){
        LocalDateTime data = LocalDateTime.now();
        ItemBySitReportCriteriaDTO criteriaDTO = ItemBySitReportCriteriaDTO.builder().initialRegDate(data).build();
        Exception e = assertThrows(IllegalArgumentException.class, ()->itemBySitReportValidator.validate(criteriaDTO));
        assertEquals("Deve ser informado o período completo (data de cadastro inicial e final).", e.getMessage());
    }

    @Test
    void invalidateFinalRegDate(){
        LocalDateTime data = LocalDateTime.now();
        ItemBySitReportCriteriaDTO criteriaDTO = ItemBySitReportCriteriaDTO.builder().initialRegDate(data).finalRegDate(data.minusDays(5)).build();
        Exception e = assertThrows(IllegalArgumentException.class, ()->itemBySitReportValidator.validate(criteriaDTO));
        assertEquals("A data de cadastro inicial não deve ser maior que a final.", e.getMessage());
    }

    @Test
    void invalidateMovementPeriod(){
        LocalDateTime data = LocalDateTime.now();
        ItemBySitReportCriteriaDTO criteriaDTO = ItemBySitReportCriteriaDTO.builder().finalMovDate(data).build();
        Exception e = assertThrows(IllegalArgumentException.class, ()->itemBySitReportValidator.validate(criteriaDTO));
        assertEquals("Deve ser informado o período completo (data de movimentação inicial e final).", e.getMessage());
    }

    @Test
    void invalidateFinalMovDate(){
        LocalDateTime data = LocalDateTime.now();
        ItemBySitReportCriteriaDTO criteriaDTO = ItemBySitReportCriteriaDTO.builder().initialMovDate(data).finalMovDate(data.minusDays(5)).build();
        Exception e = assertThrows(IllegalArgumentException.class, ()->itemBySitReportValidator.validate(criteriaDTO));
        assertEquals("A data de movimentação inicial não deve ser maior que a final.", e.getMessage());
    }
}