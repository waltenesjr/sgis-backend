package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.CurrencyTypeDTO;
import br.com.oi.sgis.dto.InterventionDTO;
import br.com.oi.sgis.dto.PriorityRepairDTO;
import br.com.oi.sgis.dto.TicketInterventionDTO;
import br.com.oi.sgis.entity.*;
import br.com.oi.sgis.enums.PriorityRepairEnum;
import br.com.oi.sgis.repository.RepairTicketRepository;
import br.com.oi.sgis.repository.TechnicalStaffRepository;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class TicketInterventionValidatorTest {

    @InjectMocks
    private TicketInterventionValidator validator;

    @Mock
    private RepairTicketRepository repairTicketRepository;
    @Mock
    private TechnicalStaffRepository technicalStaffRepository;
    @Mock
    private UnityRepository unityRepository;
    private Unity unity;
    private TechnicalStaff technicalStaff;
    private RepairTicket repairTicket;
    private TicketInterventionDTO ticketInterventionDTO;
    @BeforeEach
    void setUp(){
        unity = new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("REP").build());
        technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        technicalStaff.getDepartmentCode().setId(Utils.getUser().getDepartmentCode().getId());
        repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        repairTicket.setRepairCenterDepartment(technicalStaff.getDepartmentCode());
        ticketInterventionDTO = new EasyRandom().nextObject(TicketInterventionDTO.class);
        ticketInterventionDTO.getRepairTicket().setPriority(PriorityRepairDTO.builder().cod(PriorityRepairEnum.A.getCod()).build());
        ticketInterventionDTO.getUnity().getFiscalDocument().setCurrencyType(CurrencyTypeDTO.builder().id("EU").build());
    }

    @Test
    void validate() {
        unity.setSituationCode(Situation.builder().id("DIS").build());
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_UNITY_SIT_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void validateExternal() {
        unity.setSituationCode(Situation.builder().id("REP").build());
        ticketInterventionDTO.setExternalRepair(true);
        repairTicket.setSituation(RepSituation.builder().id("ECT").build());
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(Mockito.any());
        Mockito.doReturn(technicalStaff).when(technicalStaffRepository).getById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        assertDoesNotThrow( () -> validator.validate(ticketInterventionDTO));
    }

    @Test
    void validateExternalSituationException() {
        unity.setSituationCode(Situation.builder().id("REP").build());
        ticketInterventionDTO.setExternalRepair(true);
        repairTicket.setSituation(RepSituation.builder().id("ACT").build());
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_SITUATION_EXT_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void validateTicketInterventionValidateRepairCenterException() {
        repairTicket.setRepairCenterDepartment(new EasyRandom().nextObject(Department.class));
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_CR_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void validateTicketInterventionValidateSituation() {
        repairTicket.setSituation(RepSituation.builder().id("ABE").build());
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_SITUATION_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void validateTicketInterventionExceptionTriage() {
        ticketInterventionDTO.setIntervention(InterventionDTO.builder().id("3").build());
        technicalStaff.getDepartmentCode().setUnscreenedBlock(true);
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(Mockito.any());
        Mockito.doReturn(technicalStaff).when(technicalStaffRepository).getById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_SAVE_TRIAGE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void validateTicketInterventionExceptionTechForward() {
        ticketInterventionDTO.setIntervention(InterventionDTO.builder().id("3").build());
        technicalStaff.getDepartmentCode().setObligateFowarding(true);
        repairTicket.setSituation(RepSituation.builder().id("TESTE").build());
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(Mockito.any());
        Mockito.doReturn(technicalStaff).when(technicalStaffRepository).getById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_SAVE_FORWARDING_ERROR.getDescription(), e.getMessage());
    }
    @Test
    void validateTicketInterventionExceptionTechnician() {
        technicalStaff.getDepartmentCode().setNotDesignatedBloq(true);
        repairTicket.setRepairTechnician(null);
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(Mockito.any());
        Mockito.doReturn(technicalStaff).when(technicalStaffRepository).getById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_SAVE_TECHNICIAN_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void validateExternalInterventionException() {
        unity.setSituationCode(Situation.builder().id("REP").build());
        ticketInterventionDTO.setExternalRepair(true);
        technicalStaff.getDepartmentCode().setObligateFowarding(true);
        ticketInterventionDTO.setIntervention(InterventionDTO.builder().id("3").build());
        repairTicket.setSituation(RepSituation.builder().id("EOR").build());
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(technicalStaff).when(technicalStaffRepository).getById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_EXT_SAVE_TRIAGE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void validateExternalMantainerNullException() {
        unity.setSituationCode(Situation.builder().id("REP").build());
        ticketInterventionDTO.setExternalRepair(true);
        technicalStaff.getDepartmentCode().setNotDesignatedBloq(true);
        repairTicket.setSituation(RepSituation.builder().id("EOR").build());
        repairTicket.setMaintainer(null);
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(technicalStaff).when(technicalStaffRepository).getById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_EXT_MANTAINER_NULL_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void validateExternalMantainerException() {
        unity.setSituationCode(Situation.builder().id("REP").build());
        ticketInterventionDTO.setExternalRepair(true);
        technicalStaff.getDepartmentCode().setNotDesignatedBloq(true);
        repairTicket.setSituation(RepSituation.builder().id("EOR").build());
        repairTicket.setMaintainer(Company.builder().id("TESTE").build());
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(technicalStaff).when(technicalStaffRepository).getById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_EXT_MANTAINER_ERROR.getDescription(), e.getMessage());
    }
}