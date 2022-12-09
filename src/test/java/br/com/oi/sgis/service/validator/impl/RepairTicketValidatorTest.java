package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.entity.RepairTicket;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.exception.RepairTicketException;
import br.com.oi.sgis.service.DepartmentService;
import br.com.oi.sgis.util.MessageUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RepairTicketValidatorTest {

    @InjectMocks
    private RepairTicketValidator repairTicketValidator;

    @Mock
    private DepartmentService departmentService;

    @Test
    void validate() throws DepartmentNotFoundException {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        repairTicket.setNormalizationDate(LocalDateTime.now().minusDays(3));
        repairTicket.setCspCsDeliverDate(LocalDateTime.now().minusDays(2));
        DepartmentDTO departmentDTO = DepartmentDTO.builder().id("123").repairCenter(true).build();
        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());
        assertDoesNotThrow(()->repairTicketValidator.validate(repairTicket));
    }

    @Test
    void invalidRepairCenter() throws DepartmentNotFoundException {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        repairTicket.setNormalizationDate(LocalDateTime.now().minusDays(3));
        repairTicket.setCspCsDeliverDate(LocalDateTime.now().minusDays(2));
        DepartmentDTO departmentDTO = DepartmentDTO.builder().id("123").repairCenter(false).build();
        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());
        Exception e = assertThrows(RepairTicketException.class, ()->repairTicketValidator.validate(repairTicket));
        assertEquals(MessageUtils.REPAIR_CENTER_NOT_ABLE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void invalidCpsCsDeliverDate() throws DepartmentNotFoundException {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        repairTicket.setNormalizationDate(LocalDateTime.now().minusDays(2));
        repairTicket.setCspCsDeliverDate(LocalDateTime.now().minusDays(3));
        DepartmentDTO departmentDTO = DepartmentDTO.builder().id("123").repairCenter(true).build();
        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());
        Exception e = assertThrows(RepairTicketException.class, ()->repairTicketValidator.validate(repairTicket));
        assertEquals(MessageUtils.REPAIR_TICKET_INVALID_DELIVER_DT.getDescription(), e.getMessage());
    }

    @Test
    void invalidCpsCsDeliverDateAfterNow() throws DepartmentNotFoundException {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        repairTicket.setNormalizationDate(LocalDateTime.now());
        repairTicket.setCspCsDeliverDate(LocalDateTime.now().plusDays(3));
        DepartmentDTO departmentDTO = DepartmentDTO.builder().id("123").repairCenter(true).build();
        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());
        Exception e = assertThrows(RepairTicketException.class, ()->repairTicketValidator.validate(repairTicket));
        assertEquals(MessageUtils.REPAIR_TICKET_DELIVER_DT_AFTER_NOW.getDescription(), e.getMessage());
    }


    @Test
    void invalidNormalizationDate() throws DepartmentNotFoundException {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        repairTicket.setNormalizationDate(LocalDateTime.now().plusDays(3));
        repairTicket.setCspCsDeliverDate(LocalDateTime.now().plusDays(3));
        DepartmentDTO departmentDTO = DepartmentDTO.builder().id("123").repairCenter(true).build();
        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());
        Exception e = assertThrows(RepairTicketException.class, ()->repairTicketValidator.validate(repairTicket));
        assertEquals(MessageUtils.REPAIR_TICKET_INVALID_NORMALIZATION_DT.getDescription(), e.getMessage());
    }
}