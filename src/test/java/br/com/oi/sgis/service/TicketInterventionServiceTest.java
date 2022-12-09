package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.TicketIntervention;
import br.com.oi.sgis.enums.PriorityRepairEnum;
import br.com.oi.sgis.exception.RepSituationNotFoundException;
import br.com.oi.sgis.exception.TicketInterventionNotFoundException;
import br.com.oi.sgis.mapper.TicketInterventionMapper;
import br.com.oi.sgis.repository.ComponentMovRepository;
import br.com.oi.sgis.repository.RepairTicketRepository;
import br.com.oi.sgis.repository.TicketDiagnosisRepository;
import br.com.oi.sgis.repository.TicketInterventionRepository;
import br.com.oi.sgis.service.factory.TicketInterventionFactory;
import br.com.oi.sgis.service.validator.impl.TicketInterventionValidator;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TicketInterventionServiceTest {

    @InjectMocks
    TicketInterventionService ticketInterventionService;
    @Mock
    private TicketInterventionValidator ticketInterventionValidator;
    @Mock
    private TicketInterventionRepository ticketInterventionRepository;
    @Mock
    private TicketInterventionFactory ticketInterventionFactory;
    @Mock
    private ReportService reportService;
    @Mock
    private TicketDiagnosisRepository ticketDiagnosisRepository;
    @Mock
    private ComponentMovRepository componentMovRepository;
    @Mock
    private  RepairTicketRepository repairTicketRepository;
    @Mock
    private RepSituationService repSituationService;
    @MockBean
    private TicketInterventionMapper ticketInterventionMapper = TicketInterventionMapper.INSTANCE;

    @Mock
    private NasphService nasphService;
    private TicketInterventionDTO ticketInterventionDTO;
    private TicketIntervention ticketIntervention;

    @BeforeEach
    void setUp(){
        ticketIntervention = new EasyRandom().nextObject(TicketIntervention.class);
        ticketInterventionDTO = ticketInterventionMapper.toDTO(ticketIntervention);
        ticketInterventionDTO.getRepairTicket().setPriority(PriorityRepairDTO.builder().cod(PriorityRepairEnum.A.getCod()).build());
        ticketInterventionDTO.getUnity().getFiscalDocument().setCurrencyType(CurrencyTypeDTO.builder().id("EU").build());
    }

    @Test
    void listAllPaginated() {
        List<TicketIntervention> ticketInterventions = new EasyRandom().objects(TicketIntervention.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<TicketIntervention> pagedResult = new PageImpl<>(ticketInterventions, paging, ticketInterventions.size());

        Mockito.doReturn(pagedResult).when(ticketInterventionRepository).findLike(any(), any(Pageable.class));
        PaginateResponseDTO<TicketInterventionDTO> ticketInterventionsToReturn = ticketInterventionService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(ticketInterventions.size(), ticketInterventionsToReturn.getData().size());
    }

    @Test
    void openIntervention() {
        Mockito.doReturn(ticketIntervention).when(ticketInterventionFactory).createTicketIntervention(any());
        Mockito.doReturn(Optional.empty()).when(ticketInterventionRepository).findByUnityAndTicket(any(), any());
        MessageResponseDTO response = ticketInterventionService.openIntervention(ticketInterventionDTO);
        assertEquals(HttpStatus.CREATED, response.getStatus());

    }

    @Test
    void openInterventionExceptionExists() {
        Mockito.doReturn(Optional.of(ticketIntervention)).when(ticketInterventionRepository).findByUnityAndTicket(any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->ticketInterventionService.openIntervention(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void openInterventionException() {
        Mockito.doReturn(ticketIntervention).when(ticketInterventionFactory).createTicketIntervention(any());
        Mockito.doThrow(SqlScriptParserException.class).when(ticketInterventionRepository).save(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->ticketInterventionService.openIntervention(ticketInterventionDTO));
        assertEquals(MessageUtils.TICKET_INTERV_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void findByIdDTO() throws TicketInterventionNotFoundException {
        Mockito.doReturn(Optional.of(ticketIntervention)).when(ticketInterventionRepository).findById(any(), any());
        TicketInterventionDTO returnedTicketIntervention = ticketInterventionService.findByIdDTO(ticketInterventionDTO);
        assertEquals(ticketIntervention.getId().getSequence(), returnedTicketIntervention.getSequence());
    }

    @Test
    void findByIdDTOException() {
        Mockito.doReturn(Optional.empty()).when(ticketInterventionRepository).findById(any(), any());
        Assertions.assertThrows(TicketInterventionNotFoundException.class, () -> ticketInterventionService.findByIdDTO(ticketInterventionDTO));
    }

    @Test
    void technicianReport() throws JRException, IOException {
        TechnicianTicketDTO technicianTicketDTO = new TechnicianTicketDTO("123", "123", "123",
                "123", "1233", "1225", "484897", "8948489", "45456",
                "4545", LocalDateTime.now());
        Mockito.doReturn(List.of(technicianTicketDTO)).when(ticketInterventionRepository).getTechnicianData(any(), Mockito.anyString());
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).technicianTicketReport(any(), any());
        byte[] reportTechnicianTicket = ticketInterventionService.technicianReport("");
        assertNotNull(reportTechnicianTicket);
        assertEquals(report, reportTechnicianTicket);
    }

    @Test
    void technicianReportEmpty() {
        Mockito.doReturn(List.of()).when(ticketInterventionRepository).getTechnicianData(any(), Mockito.anyString());
        Exception e = assertThrows(IllegalArgumentException.class, () -> ticketInterventionService.technicianReport(""));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void orderServiceReport() throws JRException, IOException {
        OrderServiceDTO orderServiceDTO = new OrderServiceDTO("123", "123", "123",
                "123", "1233", "1225", "484897", "8948489", "45456",
                "4545");
        Mockito.doReturn(List.of(orderServiceDTO)).when(ticketInterventionRepository).getOrderServiceData(any(), any(), Mockito.anyString());
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).orderServiceData(any(), any());
        byte [] returnedReport = ticketInterventionService.orderServiceReport(null, null);
        assertNotNull(returnedReport);
        assertEquals(report, returnedReport);
    }

    @Test
    void orderServiceReportEmpty() {
        Mockito.doReturn(List.of()).when(ticketInterventionRepository).getOrderServiceData(any(), any(), Mockito.anyString());
        Exception e = assertThrows(IllegalArgumentException.class, ()->ticketInterventionService.orderServiceReport(null, null));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void updateInterventionSituation() throws RepSituationNotFoundException {
        SituationDTO situationDTO = new EasyRandom().nextObject(SituationDTO.class);
        Mockito.doReturn("Teste").when(nasphService).updateInterventionSituation(any());
        Mockito.doReturn(situationDTO).when(repSituationService).findById(Mockito.any());
        List<SituationDTO> situationDTOs = ticketInterventionService.updateInterventionSituation(any());
        assertEquals(1, situationDTOs.size());
    }

    @Test
    void updateInterventionSituationEmpty() throws RepSituationNotFoundException {
        SituationDTO situationDTO = new EasyRandom().nextObject(SituationDTO.class);
        Mockito.doReturn(null).when(nasphService).updateInterventionSituation(any());
        Mockito.doReturn(List.of(situationDTO)).when(repSituationService).listForwardRepairFromInterv();

        List<SituationDTO> situationDTOs = ticketInterventionService.updateInterventionSituation(any());
        assertNotNull(situationDTO);
    }

    @Test
    void updateIntervention() {
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = new EasyRandom().nextObject(TicketInterventionUpdateDTO.class);
        ticketInterventionUpdateDTO.setForwardRepair(Boolean.FALSE);
        ticketIntervention.setTicketComponents(List.of());
        ticketIntervention.setTicketDiagnosis(List.of());
        Mockito.doReturn(Optional.of(ticketIntervention)).when(ticketInterventionRepository).findById(any(), any());
        Mockito.doReturn(ticketIntervention).when(ticketInterventionFactory).createTicketIntervention(any());
        MessageResponseDTO response = ticketInterventionService.updateIntervention(ticketInterventionUpdateDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateInterventionException() {
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = new EasyRandom().nextObject(TicketInterventionUpdateDTO.class);
        ticketInterventionUpdateDTO.setForwardRepair(Boolean.FALSE);
        ticketIntervention.setTicketComponents(List.of());
        ticketIntervention.setTicketDiagnosis(List.of());
        Mockito.doReturn(Optional.of(ticketIntervention)).when(ticketInterventionRepository).findById(any(), any());
        Mockito.doReturn(ticketIntervention).when(ticketInterventionFactory).createTicketIntervention(any());
        Mockito.doThrow(SqlScriptParserException.class).when(ticketInterventionRepository).save(any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> ticketInterventionService.updateIntervention(ticketInterventionUpdateDTO));
        assertEquals(MessageUtils.TICKET_INTERV_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateInterventionForwardException() {
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = new EasyRandom().nextObject(TicketInterventionUpdateDTO.class);
        ticketInterventionUpdateDTO.setForwardRepair(Boolean.TRUE);
        ticketIntervention.setTicketComponents(List.of());
        ticketIntervention.setTicketDiagnosis(List.of());
        Mockito.doThrow(SqlScriptParserException.class).when(repairTicketRepository).updateSituation(any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> ticketInterventionService.updateIntervention(ticketInterventionUpdateDTO));
        assertEquals(MessageUtils.TICKET_INTERV_UPDATE_FORWARD_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateInterventionNewIntervention() {
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = new EasyRandom().nextObject(TicketInterventionUpdateDTO.class);
        ticketInterventionUpdateDTO.setForwardRepair(Boolean.FALSE);
        ticketIntervention.setTicketComponents(List.of());
        ticketIntervention.setTicketDiagnosis(List.of());
        Mockito.doReturn(ticketIntervention).when(ticketInterventionFactory).createTicketIntervention(any());
        MessageResponseDTO response = ticketInterventionService.updateIntervention(ticketInterventionUpdateDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

    @Test
    void updateInterventionDiagnosesAndComponents() {
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = new EasyRandom().nextObject(TicketInterventionUpdateDTO.class);
        ticketInterventionUpdateDTO.setForwardRepair(Boolean.FALSE);
        Mockito.doReturn(Optional.of(ticketIntervention)).when(ticketInterventionRepository).findById(any(), any());
        Mockito.doReturn(ticketIntervention).when(ticketInterventionFactory).createTicketIntervention(any());
        MessageResponseDTO response = ticketInterventionService.updateIntervention(ticketInterventionUpdateDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateInterventionExceptionSavingDiagnoses() {
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = new EasyRandom().nextObject(TicketInterventionUpdateDTO.class);
        ticketInterventionUpdateDTO.setForwardRepair(Boolean.FALSE);
        Mockito.doReturn(Optional.of(ticketIntervention)).when(ticketInterventionRepository).findById(any(), any());
        Mockito.doReturn(ticketIntervention).when(ticketInterventionFactory).createTicketIntervention(any());
        Mockito.doThrow(SqlScriptParserException.class).when(ticketDiagnosisRepository).saveAll(any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> ticketInterventionService.updateIntervention(ticketInterventionUpdateDTO));
        assertEquals(MessageUtils.TICKET_DIAGNOSIS_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateInterventionExceptionSavingComponents() {
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = new EasyRandom().nextObject(TicketInterventionUpdateDTO.class);
        ticketInterventionUpdateDTO.setForwardRepair(Boolean.FALSE);
        Mockito.doReturn(Optional.of(ticketIntervention)).when(ticketInterventionRepository).findById(any(), any());
        Mockito.doReturn(ticketIntervention).when(ticketInterventionFactory).createTicketIntervention(any());
        Mockito.doThrow(SqlScriptParserException.class).when(componentMovRepository).saveComponent(any(), any(), any(), any(), any(), any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> ticketInterventionService.updateIntervention(ticketInterventionUpdateDTO));
        assertEquals(MessageUtils.TICKET_COMPONENT_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateInterventionDiagnosesAndComponentsOldEmpty() {
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = new EasyRandom().nextObject(TicketInterventionUpdateDTO.class);
        ticketInterventionUpdateDTO.setForwardRepair(Boolean.FALSE);
        TicketIntervention oldEmpty = new EasyRandom().nextObject(TicketIntervention.class);
        oldEmpty.setTicketDiagnosis(List.of());
        oldEmpty.setTicketComponents(List.of());
        Mockito.doReturn(Optional.of(oldEmpty)).when(ticketInterventionRepository).findById(any(), any());
        Mockito.doReturn(ticketIntervention).when(ticketInterventionFactory).createTicketIntervention(any());
        MessageResponseDTO response = ticketInterventionService.updateIntervention(ticketInterventionUpdateDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
    }
    @Test
    void updateInterventionDiagnosesAndComponentsNewEmpty() {
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = new EasyRandom().nextObject(TicketInterventionUpdateDTO.class);
        ticketInterventionUpdateDTO.setForwardRepair(Boolean.FALSE);
        TicketIntervention newEmpty = new EasyRandom().nextObject(TicketIntervention.class);
        newEmpty.setTicketDiagnosis(List.of());
        newEmpty.setTicketComponents(List.of());
        Mockito.doReturn(Optional.of(ticketIntervention)).when(ticketInterventionRepository).findById(any(), any());
        Mockito.doReturn(newEmpty).when(ticketInterventionFactory).createTicketIntervention(any());
        MessageResponseDTO response = ticketInterventionService.updateIntervention(ticketInterventionUpdateDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void listAllToClosePaginated() {
        List<TicketIntervention> ticketInterventions = new EasyRandom().objects(TicketIntervention.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<TicketIntervention> pagedResult = new PageImpl<>(ticketInterventions, paging, ticketInterventions.size());

        Mockito.doReturn(pagedResult).when(ticketInterventionRepository).findLikeToClose(any(), any(Pageable.class));
        PaginateResponseDTO<TicketInterventionDTO> ticketInterventionsToReturn = ticketInterventionService.listAllToClosePaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(ticketInterventions.size(), ticketInterventionsToReturn.getData().size());
    }

    @Test
    void closeIntervention() {
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = TicketInterventionUpdateDTO.builder()
                .ticketIntervention(ticketInterventionDTO).forwardRepair(Boolean.FALSE).build();
        ticketIntervention.setTicketComponents(List.of());
        ticketIntervention.setTicketDiagnosis(List.of());
        Mockito.doReturn(Optional.of(ticketIntervention)).when(ticketInterventionRepository).findById(any(), any());
        MessageResponseDTO response = ticketInterventionService.closeIntervention(ticketInterventionUpdateDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void closeInterventionException() {
        TicketInterventionUpdateDTO ticketInterventionUpdateDTO = TicketInterventionUpdateDTO.builder()
                .ticketIntervention(ticketInterventionDTO).forwardRepair(Boolean.FALSE).build();
        ticketIntervention.setTicketComponents(List.of());
        ticketIntervention.setTicketDiagnosis(List.of());
        Mockito.doReturn(Optional.of(ticketIntervention)).when(ticketInterventionRepository).findById(any(), any());
        Mockito.doThrow(SqlScriptParserException.class).when(ticketInterventionRepository).closeIntervention(any(), any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> ticketInterventionService.closeIntervention(ticketInterventionUpdateDTO));
        assertEquals(MessageUtils.TICKET_INTERV_CLOSE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void listAllToUpdatePaginated() {
        List<TicketIntervention> ticketInterventions = new EasyRandom().objects(TicketIntervention.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<TicketIntervention> pagedResult = new PageImpl<>(ticketInterventions, paging, ticketInterventions.size());

        Mockito.doReturn(pagedResult).when(ticketInterventionRepository).findLikeToUpdate(any(), any(), any(), any(Pageable.class));
        PaginateResponseDTO<TicketInterventionDTO> ticketInterventionsToReturn = ticketInterventionService.listAllToUpdatePaginated(0, 10, List.of("id"), List.of("date"), "", "123");
        assertEquals(ticketInterventions.size(), ticketInterventionsToReturn.getData().size());
    }

    @Test
    void technicianTimesReport() throws JRException, IOException {
        TechnicianTimesFilterDTO filterDTO = new EasyRandom().nextObject(TechnicianTimesFilterDTO.class);
        filterDTO.setInitialDate(LocalDateTime.now());filterDTO.setFinalDate(LocalDateTime.now().plusDays(5));
        TechnicianTimesDTO technicianTimesDTO = new EasyRandom().nextObject(TechnicianTimesDTO.class);
        Mockito.doReturn(List.of(technicianTimesDTO)).when(ticketInterventionRepository).findTechnicianTimes(any());
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).technicianTimesReport(any(), any());
        byte[] technicianTimesReport = ticketInterventionService.technicianTimesReport(filterDTO);
        assertEquals(report, technicianTimesReport);
    }

    @Test
    void technicianTimesReportEmpty() {
        TechnicianTimesFilterDTO filterDTO = new EasyRandom().nextObject(TechnicianTimesFilterDTO.class);
        filterDTO.setInitialDate(LocalDateTime.now());filterDTO.setFinalDate(LocalDateTime.now().plusDays(5));
        Mockito.doReturn(List.of()).when(ticketInterventionRepository).findTechnicianTimes(any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> ticketInterventionService.technicianTimesReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void technicianTimesReportError() throws JRException, IOException {
        TechnicianTimesFilterDTO filterDTO = new EasyRandom().nextObject(TechnicianTimesFilterDTO.class);
        filterDTO.setInitialDate(LocalDateTime.now());filterDTO.setFinalDate(LocalDateTime.now().plusDays(5));
        TechnicianTimesDTO technicianTimesDTO = new EasyRandom().nextObject(TechnicianTimesDTO.class);
        Mockito.doReturn(List.of(technicianTimesDTO)).when(ticketInterventionRepository).findTechnicianTimes(any());
        Mockito.doThrow(JRException.class).when(reportService).technicianTimesReport(any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> ticketInterventionService.technicianTimesReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }
}