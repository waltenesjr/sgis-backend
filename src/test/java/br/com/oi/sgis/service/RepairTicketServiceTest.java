package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.BarcodeFAS;
import br.com.oi.sgis.entity.DepartmentUnity;
import br.com.oi.sgis.entity.RepairTicket;
import br.com.oi.sgis.entity.view.WarrantyView;
import br.com.oi.sgis.enums.CostComparisonReportBreakEnum;
import br.com.oi.sgis.enums.PriorityRepairEnum;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.exception.RepSituationNotFoundException;
import br.com.oi.sgis.exception.RepairTicketException;
import br.com.oi.sgis.mapper.RepairTicketMapper;
import br.com.oi.sgis.repository.*;
import br.com.oi.sgis.repository.impl.ProductivityComparisonRepositoryCustomImpl;
import br.com.oi.sgis.service.factory.RepairTicketFactory;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class RepairTicketServiceTest {

    @InjectMocks
    private RepairTicketService repairTicketService;

    @Mock
    private UnityService unityService;
    @Mock
    private RepSituationService repSituationService;
    @Mock
    private BarcodeFASRepository barcodeFASRepository;
    @Mock
    private RepairTicketFactory repairTicketFactory;
    @Mock
    private RepairTicketRepository repairTicketRepository;
    @MockBean @Spy
    private RepairTicketMapper repairTicketMapper = RepairTicketMapper.INSTANCE;
    @Mock
    private Validator<RepairTicket> validator;
    @Mock
    private TicketRepExtraInfoService ticketRepExtraInfoService;
    @Mock
    private DepartmentUnityRepository departmentUnityRepository;
    @Mock
    private NasphService nasphService;
    @Mock
    private RepairTicketAnalysisService repairTicketAnalysisService;
    @Mock
    private ReportService reportService;
    @Mock
    private WarrantyViewRepository warrantyViewRepository;
    @Mock
    private EquipmentTypeRepairRepository equipmentTypeRepairRepository;

    @Mock
    private ProductivityComparisonRepositoryCustomImpl productivityComparisonRepository;

    @Mock
    private CostComparisonService costComparisonService;

    @Test
    void getUnity() {
        UnityDTO unityDTO = UnityDTO.builder().id("1")
                .situationCode(SituationDTO.builder().id(SituationEnum.DIS.getCod()).build()).build();

        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        UnityDTO retrunedUnity = repairTicketService.getUnity("1");

        assertEquals(unityDTO.getId(), retrunedUnity.getId());
    }

    @Test
    void getUnityThrowExceptionRepSit() {
        UnityDTO unityDTO = UnityDTO.builder().id("1")
                .situationCode(SituationDTO.builder().id(SituationEnum.REP.getCod()).build()).build();

        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> repairTicketService.getUnity("1"));
        assertEquals(MessageUtils.UNITY_ALREADY_REP_SITUATION_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void getUnityThrowExceptionCannotOpenRepSit() {
        UnityDTO unityDTO = UnityDTO.builder().id("1")
                .situationCode(SituationDTO.builder().id(SituationEnum.TRR.getCod()).build()).build();

        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> repairTicketService.getUnity("1"));
        assertEquals(MessageUtils.UNITY_SITUATION_REPAIR_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void getDefaultRepSituation() throws RepSituationNotFoundException {
        SituationDTO situationDTO = SituationDTO.builder().id("ABE").build();
        Mockito.doReturn(situationDTO).when(repSituationService).findById(any());

        assertEquals(situationDTO.getId(), repairTicketService.getDefaultRepSituation().getId());
    }

    @Test
    void fasNumberNotPresent() {
        MessageResponseDTO messageResponseDTO = repairTicketService.fasNumber("");
        assertEquals(MessageUtils.UNITY_BLANK_FAS_N_REPAIR.getDescription(), messageResponseDTO.getMessage());
    }

    @Test
    void fasNumber() {
        BarcodeFAS barcodeFAS = BarcodeFAS.builder().id("122352").build();
        Mockito.doReturn(Optional.of(barcodeFAS)).when(barcodeFASRepository).findById(any());
        MessageResponseDTO messageResponseDTO = repairTicketService.fasNumber("122352");
        assertEquals(MessageUtils.UNITY_EXISTS_FAS_N_REPAIR.getDescription(), messageResponseDTO.getMessage());
    }

    @Test
    void fasNumberNotExists() {
        Mockito.doReturn(Optional.empty()).when(barcodeFASRepository).findById(any());
        MessageResponseDTO messageResponseDTO = repairTicketService.fasNumber("122352");
        assertEquals(MessageUtils.UNITY_NOT_EXISTS_FAS_N_REPAIR.getDescription(), messageResponseDTO.getMessage());
    }

    @Test
    void createRepairTicket() throws RepairTicketException {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        Mockito.doReturn(repairTicket).when(repairTicketFactory).createRepairTicket(any());

        MessageResponseDTO messageResponseDTO = repairTicketService.createRepairTicket(new RepairTicketDTO());
        assertEquals(MessageUtils.REPAIR_TICKET_SUCCESS.getDescription() + repairTicket.getId(), messageResponseDTO.getMessage());

    }

    @Test
    void createRepairTicketWithException() throws RepairTicketException {
        Mockito.doThrow(RepairTicketException.class).when(repairTicketFactory).createRepairTicket(any());

        assertThrows(RepairTicketException.class, () -> repairTicketService.createRepairTicket(new RepairTicketDTO()));

    }

    @Test
    void listAllPaginated() {
        List<RepairTicket> repairTickets = new EasyRandom().objects(RepairTicket.class, 5).collect(Collectors.toList());
        repairTickets.forEach(x -> x.setPriority(PriorityRepairEnum.M));
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<RepairTicket> pagedResult = new PageImpl<>(repairTickets, paging, repairTickets.size());

        Mockito.doReturn(pagedResult).when(repairTicketRepository).findLike(anyString(), any(Pageable.class));
        PaginateResponseDTO<Object> repairTicketsToReturn = repairTicketService.listAllPaginated(0, 10, List.of("id"), null, "teste");
        Assertions.assertEquals(repairTickets.size(), repairTicketsToReturn.getData().size());
    }

    @Test
    void listAllPaginatedWithoutTerm(){
        List<RepairTicket> repairTickets = new EasyRandom().objects(RepairTicket.class, 5).collect(Collectors.toList());
        repairTickets.forEach(x ->  x.setPriority(PriorityRepairEnum.M));
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<RepairTicket> pagedResult = new PageImpl<>(repairTickets, paging, repairTickets.size());

        Mockito.doReturn(pagedResult).when(repairTicketRepository).findAll(any(Pageable.class));
        PaginateResponseDTO<Object> repairTicketsToReturn = repairTicketService.listAllPaginated(0, 10, List.of("id"), null, "");
        Assertions.assertEquals(repairTickets.size(), repairTicketsToReturn.getData().size());
    }
    @Test
    void updateRepairTicket() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        repairTicketDTO.setPriority(PriorityRepairDTO.builder().cod("A").build());
        FiscalDocumentDTO fiscalDocumentDTO = new FiscalDocumentDTO();
        fiscalDocumentDTO.setCurrencyType(CurrencyTypeDTO.builder().id("RE").build());
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        unityDTO.setFiscalDocument(fiscalDocumentDTO);
        unityDTO.setSituationCode(SituationDTO.builder().id("TRR").build());
        Mockito.doReturn(unityDTO).when(unityService).findById(any());

        MessageResponseDTO messageResponseDTO = repairTicketService.updateRepairTicket(repairTicketDTO);
        assertEquals(MessageUtils.REPAIR_TICKET_UPDATE_SUCCESS.getDescription() + repairTicketDTO.getBrNumber(), messageResponseDTO.getMessage());
    }

//    @Test
//    void updateRepairTicketException() {
//        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
//        repairTicketDTO.setPriority(PriorityRepairDTO.builder().cod("A").build());
//        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
//        Mockito.doReturn(unityDTO).when(unityService).findById(Mockito.any());
//
//        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.updateRepairTicket(repairTicketDTO));
//        assertEquals(MessageUtils.REPAIR_TICKET_SITUATION_UPDT_ERROR.getDescription(), e.getMessage());
//    }

    @Test
    void getExtraInformation() throws RepairTicketException {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        repairTicket.setPriority( PriorityRepairEnum.M);
        TicketRepExtraInfoDTO extraInfoDTO = new EasyRandom().nextObject(TicketRepExtraInfoDTO.class);
        Mockito.doReturn(extraInfoDTO).when(ticketRepExtraInfoService).getExtraInfo(any());
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(any());

        TicketRepExtraInfoDTO returnedExtraInfoDTO = repairTicketService.getExtraInformation("repairTicket");

        assertEquals(extraInfoDTO.getAcceptDate(),returnedExtraInfoDTO.getAcceptDate());
    }

    @Test
    void findById() throws RepairTicketException {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        repairTicket.setPriority(PriorityRepairEnum.M);
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(any());
        RepairTicketDTO repairTicketToReturn = repairTicketService.findById("1L");

        Assertions.assertEquals(repairTicket.getId(), repairTicketToReturn.getBrNumber());
    }
    @Test
    void shouldFindByIdWithException() {
        Mockito.doReturn(Optional.empty()).when(repairTicketRepository).findById(any());

        Assertions.assertThrows(RepairTicketException.class, () -> repairTicketService.findById("1L"));
    }

    @Test
    void getAcceptRepair() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        Mockito.doReturn(Optional.of(repairTicketDTO)).when(repairTicketRepository).findTopByUnityIdAndSituationId(any(), anyString());
        Mockito.doReturn(Optional.empty()).when(departmentUnityRepository).findById(any());

        AcceptTicketRepairDTO acceptTicketRepairDTO = repairTicketService.getAcceptRepair("1L");
        assertEquals(unityDTO.getId(), acceptTicketRepairDTO.getUnityId());
        assertEquals(unityDTO.getStation(), acceptTicketRepairDTO.getStationDTO());
        assertEquals(unityDTO.getLocation(), acceptTicketRepairDTO.getLocation());
    }

    @Test
    void getAcceptRepairWithDepartmentUnity() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        DepartmentUnity departmentUnity = new EasyRandom().nextObject(DepartmentUnity.class);
        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        Mockito.doReturn(Optional.of(repairTicketDTO)).when(repairTicketRepository).findTopByUnityIdAndSituationId(any(), anyString());
        Mockito.doReturn(Optional.of(departmentUnity)).when(departmentUnityRepository).findById(any());

        AcceptTicketRepairDTO acceptTicketRepairDTO = repairTicketService.getAcceptRepair("1L");
        assertEquals(unityDTO.getId(), acceptTicketRepairDTO.getUnityId());
        assertEquals(departmentUnity.getStation().getId(), acceptTicketRepairDTO.getStationDTO().getId());
        assertEquals(departmentUnity.getLocation(), acceptTicketRepairDTO.getLocation());
    }

    @Test
    void getAcceptRepairExceptionNotExistsTicket() {
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        Mockito.doReturn(Optional.empty()).when(repairTicketRepository).findTopByUnityIdAndSituationId(any(), anyString());

        Exception exception = assertThrows(RepairTicketException.class, () -> repairTicketService.getAcceptRepair("1L"));
        assertEquals(MessageUtils.REPAIR_TICKET_OPEN_NOT_FOUND_BY_UNITY.getDescription(), exception.getMessage());
    }

    @Test
    void verifyWarrantyDate() {
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        ResponseEntity<MessageResponseDTO> returnedResponse = repairTicketService.verifyWarrantyDate("1L");

        assertEquals(HttpStatus.ACCEPTED, returnedResponse.getStatusCode());
    }

    @Test
    void verifyWarrantyDateWhenNull() {
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        unityDTO.setWarrantyDate(null);
        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        ResponseEntity<MessageResponseDTO> returnedResponse = repairTicketService.verifyWarrantyDate("1L");

        assertEquals(HttpStatus.NO_CONTENT, returnedResponse.getStatusCode());
    }

    @Test
    void acceptRepair() {
        AcceptTicketRepairDTO acceptTicketRepairDTO = new EasyRandom().nextObject(AcceptTicketRepairDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).message(MessageUtils.REPAIR_TICKET_ACCEPT_SUCCESS.getDescription()).build();
        Mockito.doReturn(responseDTO).when(nasphService).acceptRepairTicket(any());
        MessageResponseDTO returnedResponse = repairTicketService.acceptRepair(acceptTicketRepairDTO);

        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
        assertEquals(responseDTO.getMessage(), returnedResponse.getMessage());

    }

    @Test
    void getForwardRepair() {
        List<RepairTicket> repairTickets = new EasyRandom().objects(RepairTicket.class, 5).collect(Collectors.toList());
        repairTickets.forEach(x -> x.setPriority(PriorityRepairEnum.M));
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<RepairTicket> pagedResult = new PageImpl<>(repairTickets, paging, repairTickets.size());
        ForwardTicketDTO dto = new EasyRandom().nextObject(ForwardTicketDTO.class);
        dto.setInitialDate(null);
        dto.setFinalDate(null);
        Mockito.doReturn(pagedResult).when(repairTicketRepository).findTicketsToForward(any(), anyString(), any(Pageable.class));
        PaginateResponseDTO<RepairTicketDTO> repairTicketsToReturn = repairTicketService.getForwardRepair(dto, 0, 10, List.of("id"), null);

        Assertions.assertEquals(repairTickets.size(), repairTicketsToReturn.getData().size());
    }

    @Test
    void getForwardRepairWithPeriod() {
        List<RepairTicket> repairTickets = new EasyRandom().objects(RepairTicket.class, 5).collect(Collectors.toList());
        repairTickets.forEach(x -> x.setPriority(PriorityRepairEnum.M));
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<RepairTicket> pagedResult = new PageImpl<>(repairTickets, paging, repairTickets.size());
        ForwardTicketDTO dto = new EasyRandom().nextObject(ForwardTicketDTO.class);
        dto.setInitialDate(LocalDateTime.now());
        dto.setFinalDate(LocalDateTime.now().plusDays(5));
        Mockito.doReturn(pagedResult).when(repairTicketRepository).findTicketsToForwardWithDateFilter(any(), anyString(), any(Pageable.class));
        PaginateResponseDTO<RepairTicketDTO> repairTicketsToReturn = repairTicketService.getForwardRepair(dto, 0, 10, List.of("id"), null);

        Assertions.assertEquals(repairTickets.size(), repairTicketsToReturn.getData().size());
    }

    @Test
    void ticketToForward() throws RepairTicketException {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        repairTicket.setPriority(PriorityRepairEnum.M);
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(any());
        Mockito.doReturn(0).when(repairTicketRepository).findForward(anyString());
        RepairTicketDTO returnedTicket = repairTicketService.ticketToForward("RJ2019000004");
        assertEquals(repairTicket.getId(), returnedTicket.getBrNumber());
    }

    @Test
    void ticketToForwardException() {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        repairTicket.setPriority(PriorityRepairEnum.M);
        Mockito.doReturn(Optional.of(repairTicket)).when(repairTicketRepository).findById(any());
        Mockito.doReturn(1).when(repairTicketRepository).findForward(anyString());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.ticketToForward("RJ2019000004"));
        assertEquals(MessageUtils.INVALID_FORWARD_TICKET.getDescription(), e.getMessage());
    }

    @Test
    void forwardRepair() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        MessageResponseDTO response = repairTicketService.forwardRepair(repairTicketDTO);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void forwardRepairException() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        Mockito.doThrow(SqlScriptParserException.class).when(repairTicketRepository).forwardRepair(any(), any(), any(), any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.forwardRepair(repairTicketDTO));
        assertEquals(MessageUtils.REPAIR_TICKET_FORWARD_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void forwardTicketReport() throws JRException, IOException {
        List<RepairTicket> repairTickets = new EasyRandom().objects(RepairTicket.class, 5).collect(Collectors.toList());
        repairTickets.forEach(x -> x.setPriority(PriorityRepairEnum.M));
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<RepairTicket> pagedResult = new PageImpl<>(repairTickets, paging, repairTickets.size());
        ForwardTicketDTO dto = new EasyRandom().nextObject(ForwardTicketDTO.class);
        dto.setInitialDate(LocalDateTime.now());
        dto.setFinalDate(LocalDateTime.now().plusDays(5));
        Mockito.doReturn(pagedResult).when(repairTicketRepository).findTicketsToForwardWithDateFilter(any(), anyString(), any(Pageable.class));
        byte[] report = new byte[100];
        Mockito.doReturn(report).when(reportService).forwardTicketReport(any(), any());
        byte [] response = repairTicketService.forwardTicketReport(dto, List.of(), List.of());
        assertNotNull(response);
    }

    @Test
    void forwardTicketReportEmpty() {
        List<RepairTicket> repairTickets = List.of();
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<RepairTicket> pagedResult = new PageImpl<>(repairTickets, paging, 0);
        ForwardTicketDTO dto = new EasyRandom().nextObject(ForwardTicketDTO.class);
        dto.setInitialDate(null);
        dto.setFinalDate(null);
        Mockito.doReturn(pagedResult).when(repairTicketRepository).findTicketsToForward(any(), anyString(), any(Pageable.class));
        List<String> sorts = List.of();
        Exception e = assertThrows(IllegalArgumentException.class, () -> repairTicketService.forwardTicketReport(dto, sorts, sorts));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void closeRepair() {
        CloseRepairTickectDTO closeRepairTickectDTO = new EasyRandom().nextObject(CloseRepairTickectDTO.class);
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).message(MessageUtils.CLOSE_REPAIR_SUCCESS.getDescription()).build();
        Mockito.doReturn(responseDTO).when(nasphService).closeRepair(any());
        MessageResponseDTO returnedResponse = repairTicketService.closeRepair(closeRepairTickectDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void closeRepairSituation() throws RepSituationNotFoundException {
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        Mockito.doReturn("TESTE").when(nasphService).closeRepairSituation(any());
        Mockito.doReturn(SituationDTO.builder().id("TESTE").build()).when(repSituationService).findById(any());
        SituationDTO situationDTO = repairTicketService.closeRepairSituation("123");
        assertNotNull(situationDTO);
    }

    @Test
    void closeRepairSituationNull()  {
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        Mockito.doReturn(null).when(nasphService).closeRepairSituation(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()-> repairTicketService.closeRepairSituation("123"));
        assertEquals(MessageUtils.INVALID_CLOSE_SITUATION.getDescription(), e.getMessage());
    }

    @Test
    void devolutionRepair() {
        DevolutionRepairTicketDTO devolutionRepairTicketDTO = new EasyRandom().nextObject(DevolutionRepairTicketDTO.class);
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).message(MessageUtils.DEVOLUTION_REPAIR_SUCCESS.getDescription()).build();
        Mockito.doReturn(responseDTO).when(nasphService).devolutionRepair(any());
        MessageResponseDTO returnedResponse = repairTicketService.devolutionRepair(devolutionRepairTicketDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void cancelRepair() {
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        Mockito.doReturn(unityDTO).when(unityService).findById(any());
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).message(MessageUtils.CANCEL_REPAIR_SUCCESS.getDescription()).build();
        Mockito.doReturn(responseDTO).when(nasphService).cancelRepair(any(), anyString());
        MessageResponseDTO returnedResponse = repairTicketService.cancelRepair("1111");
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void getDesignateTechnician() {
        List<RepairTicket> repairTickets = new EasyRandom().objects(RepairTicket.class, 5).collect(Collectors.toList());
        repairTickets.forEach(x -> x.setPriority(PriorityRepairEnum.M));
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<RepairTicket> pagedResult = new PageImpl<>(repairTickets, paging, repairTickets.size());
        DesignateTechnicianDTO dto = new EasyRandom().nextObject(DesignateTechnicianDTO.class);

        Mockito.doReturn(pagedResult).when(repairTicketRepository).findDesignateTechnician(any(), anyString(), any(Pageable.class));
        PaginateResponseDTO<RepairTicketDTO> repairTicketsToReturn = repairTicketService.getDesignateTechnician(dto, 0, 10, List.of("id"), null);

        Assertions.assertEquals(repairTickets.size(), repairTicketsToReturn.getData().size());
    }

    @Test
    void designateTechnician() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        MessageResponseDTO response = repairTicketService.designateTechnician(repairTicketDTO);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void designateTechnicianException() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        Mockito.doThrow(SqlScriptParserException.class).when(repairTicketRepository).designateTechnician(any(), any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.designateTechnician(repairTicketDTO));
        assertEquals(MessageUtils.REPAIR_TICKET_DESIG_TECH_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void getTicketsForIntervention() {
        List<RepairTicket> repairTickets = new EasyRandom().objects(RepairTicket.class, 5).collect(Collectors.toList());
        repairTickets.forEach(x -> x.setPriority(PriorityRepairEnum.M));
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<RepairTicket> pagedResult = new PageImpl<>(repairTickets, paging, repairTickets.size());

        Mockito.doReturn(pagedResult).when(repairTicketRepository).findTicketsForInterventionLike(any(), anyString(), any(Pageable.class));
        PaginateResponseDTO<RepairTicketDTO> repairTicketsToReturn = repairTicketService.getTicketsForIntervention(0, 10, List.of("id"), null, "");

        Assertions.assertEquals(repairTickets.size(), repairTicketsToReturn.getData().size());
    }

    @Test
    void repairSummaryReport() throws JRException, IOException {
        RepairSummaryFilterDTO filterDTO = RepairSummaryFilterDTO.builder().quantity(true).values(false).build();
        List<RepairSummaryQuantityDTO> summaryDTOS = new EasyRandom().objects(RepairSummaryQuantityDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(summaryDTOS).when(repairTicketRepository).getQuantitySummary(any(), any(), any());
        Mockito.doReturn(new byte[50]).when(reportService).summaryQuantityRepairReport(any(), any());
        byte[] report = repairTicketService.repairSummaryReport(filterDTO);
        assertNotNull(report);
    }

    @Test
    void repairSummaryReportQuantityEmpty() {
        RepairSummaryFilterDTO filterDTO = RepairSummaryFilterDTO.builder().quantity(true).values(false).build();
        Mockito.doReturn(List.of()).when(repairTicketRepository).getQuantitySummary(any(), any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.repairSummaryReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }


    @Test
    void repairSummaryReportQuantityError() throws JRException, IOException {
        RepairSummaryFilterDTO filterDTO = RepairSummaryFilterDTO.builder().quantity(true).values(false).build();
        List<RepairSummaryQuantityDTO> summaryDTOS = new EasyRandom().objects(RepairSummaryQuantityDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(summaryDTOS).when(repairTicketRepository).getQuantitySummary(any(), any(), any());
        Mockito.doThrow(JRException.class).when(reportService).summaryQuantityRepairReport(any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.repairSummaryReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void repairSummaryValuesReport() throws JRException, IOException {
        RepairSummaryFilterDTO filterDTO = RepairSummaryFilterDTO.builder().quantity(false).values(true).build();
        List<RepairSummaryValueDTO> summaryDTOS = new EasyRandom().objects(RepairSummaryValueDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(summaryDTOS).when(repairTicketRepository).getValuesSummary(any(), any(), any());
        Mockito.doReturn(new byte[50]).when(reportService).summaryValueRepairReport(any(), any());
        byte[] report = repairTicketService.repairSummaryReport(filterDTO);
        assertNotNull(report);
    }

    @Test
    void repairSummaryReportValuesEmpty() {
        RepairSummaryFilterDTO filterDTO = RepairSummaryFilterDTO.builder().quantity(false).values(true).build();
        Mockito.doReturn(List.of()).when(repairTicketRepository).getValuesSummary(any(), any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.repairSummaryReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }


    @Test
    void repairSummaryReportValuesError() throws JRException, IOException {
        RepairSummaryFilterDTO filterDTO = RepairSummaryFilterDTO.builder().quantity(false).values(true).build();
        List<RepairSummaryValueDTO> summaryDTOS = new EasyRandom().objects(RepairSummaryValueDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(summaryDTOS).when(repairTicketRepository).getValuesSummary(any(), any(), any());
        Mockito.doThrow(JRException.class).when(reportService).summaryValueRepairReport(any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.repairSummaryReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void analyticReport() throws JRException, IOException {
        RepairAnalyticFilterDTO filterDTO = new EasyRandom().nextObject(RepairAnalyticFilterDTO.class);
        filterDTO.setInitialRepairDate(LocalDateTime.now().minusDays(5));
        filterDTO.setFinalRepairDate(LocalDateTime.now().plusDays(5));
        filterDTO.setInitialWarrantyDate(LocalDateTime.now());
        filterDTO.setFinalWarrantyDate(LocalDateTime.now().plusDays(5));
        List<AnalyticRepairDTO> analyticRepairDTOS = new EasyRandom().objects(AnalyticRepairDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(analyticRepairDTOS).when(repairTicketRepository).getAnalytics(any());
        Mockito.doReturn(new byte[50]).when(repairTicketAnalysisService).report(any(), any());
        byte[] report = repairTicketService.analyticReport(filterDTO);
        assertNotNull(report);
    }

    @Test
    void analyticReportEmpty() {
        RepairAnalyticFilterDTO filterDTO = new EasyRandom().nextObject(RepairAnalyticFilterDTO.class);
        filterDTO.setInitialRepairDate(LocalDateTime.now().minusDays(5));
        filterDTO.setFinalRepairDate(LocalDateTime.now().plusDays(5));
        filterDTO.setInitialWarrantyDate(LocalDateTime.now());
        filterDTO.setFinalWarrantyDate(LocalDateTime.now().plusDays(5));
        Mockito.doReturn(List.of()).when(repairTicketRepository).getAnalytics(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.analyticReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void analyticReportEmptyError() throws JRException, IOException {
        RepairAnalyticFilterDTO filterDTO = new EasyRandom().nextObject(RepairAnalyticFilterDTO.class);
        filterDTO.setInitialRepairDate(LocalDateTime.now().minusDays(5));
        filterDTO.setFinalRepairDate(LocalDateTime.now().plusDays(5));
        filterDTO.setInitialWarrantyDate(LocalDateTime.now());
        filterDTO.setFinalWarrantyDate(LocalDateTime.now().plusDays(5));
        List<RepairSummaryValueDTO> summaryDTOS = new EasyRandom().objects(RepairSummaryValueDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(summaryDTOS).when(repairTicketRepository).getAnalytics(any());
        Mockito.doThrow(JRException.class).when(repairTicketAnalysisService).report(any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.analyticReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void externalRepairReport() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        ExternalRepairFilterDTO filterDTO = ExternalRepairFilterDTO.builder().finalRepairDate(finalDate).finalArriveDate(finalDate).finalExitDate(finalDate)
                .finalEstimateDate(finalDate).finalWarrantyDate(finalDate).finalAcceptanceDate(finalDate).initialRepairDate(initialDate).initialArriveDate(initialDate).initialExitDate(initialDate)
                .initialEstimateDate(initialDate).initialWarrantyDate(initialDate).initialAcceptanceDate(initialDate).build();
        List<ExternalRepairReportDTO> externalRepairReportDTOS = new EasyRandom().objects(ExternalRepairReportDTO.class, 5).collect(Collectors.toList());
        WarrantyView warrantyView = new EasyRandom().nextObject(WarrantyView.class);
        Mockito.doReturn(warrantyView).when(warrantyViewRepository).findOneByUnityId(any());
        Mockito.doReturn(externalRepairReportDTOS).when(repairTicketRepository).getExternalRepair(any());
        Mockito.doReturn(new byte[50]).when(reportService).externalRepairReport(any());
        byte[] report = repairTicketService.externalRepairReport(filterDTO);
        assertNotNull(report);
    }

    @Test
    void externalRepairReportEmpty() {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        ExternalRepairFilterDTO filterDTO = ExternalRepairFilterDTO.builder().finalRepairDate(finalDate).finalArriveDate(finalDate).finalExitDate(finalDate)
                .finalEstimateDate(finalDate).finalWarrantyDate(finalDate).finalAcceptanceDate(finalDate).initialRepairDate(initialDate).initialArriveDate(initialDate).initialExitDate(initialDate)
                .initialEstimateDate(initialDate).initialWarrantyDate(initialDate).initialAcceptanceDate(initialDate).build();
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.externalRepairReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void externalRepairReportError() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        ExternalRepairFilterDTO filterDTO = ExternalRepairFilterDTO.builder().finalRepairDate(finalDate).finalArriveDate(finalDate).finalExitDate(finalDate)
                .finalEstimateDate(finalDate).finalWarrantyDate(finalDate).finalAcceptanceDate(finalDate).initialRepairDate(initialDate).initialArriveDate(initialDate).initialExitDate(initialDate)
                .initialEstimateDate(initialDate).initialWarrantyDate(initialDate).initialAcceptanceDate(initialDate).build();
        List<ExternalRepairReportDTO> externalRepairReportDTOS = new EasyRandom().objects(ExternalRepairReportDTO.class, 5).collect(Collectors.toList());
        WarrantyView warrantyView = new EasyRandom().nextObject(WarrantyView.class);
        Mockito.doReturn(warrantyView).when(warrantyViewRepository).findOneByUnityId(any());
        Mockito.doReturn(externalRepairReportDTOS).when(repairTicketRepository).getExternalRepair(any());
        Mockito.doThrow(JRException.class).when(reportService).externalRepairReport(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.externalRepairReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void operatorTicketReport() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        OperatorTicketFilterDTO filterDTO = OperatorTicketFilterDTO.builder().finalRepairDate(finalDate).initialRepairDate(initialDate).build();
        List<OperatorTicketDTO> operatorTicketDTOS = new EasyRandom().objects(OperatorTicketDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(operatorTicketDTOS).when(repairTicketRepository).getOperatorTicket(any());
        Mockito.doReturn(new byte[50]).when(reportService).operatorTicketReport(any());
        byte[] report = repairTicketService.operatorTicketReport(filterDTO);
        assertNotNull(report);
    }

    @Test
    void operatorTicketReportEmpty()  {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        OperatorTicketFilterDTO filterDTO = OperatorTicketFilterDTO.builder().finalRepairDate(finalDate).initialRepairDate(initialDate).build();
        List<OperatorTicketDTO> operatorTicketDTOS = List.of();
        Mockito.doReturn(operatorTicketDTOS).when(repairTicketRepository).getOperatorTicket(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.operatorTicketReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void operatorTicketReportError() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        OperatorTicketFilterDTO filterDTO = OperatorTicketFilterDTO.builder().finalRepairDate(finalDate).initialRepairDate(initialDate).build();
        List<OperatorTicketDTO> operatorTicketDTOS = new EasyRandom().objects(OperatorTicketDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(operatorTicketDTOS).when(repairTicketRepository).getOperatorTicket(any());
        Mockito.doThrow(JRException.class).when(reportService).operatorTicketReport(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.operatorTicketReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void ticketReleasedForReturnReport() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        TicketReleasedFilterDTO filterDTO = TicketReleasedFilterDTO.builder().initialOpenDate(initialDate).finalOpenDate(finalDate).initialCloseDate(initialDate).finalCloseDate(finalDate).build();
        List<TicketReleasedDTO> ticketReleasedDTOS = new EasyRandom().objects(TicketReleasedDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(ticketReleasedDTOS).when(repairTicketRepository).getTicketReleased(any());
        Mockito.doReturn(new byte[50]).when(reportService).ticketReleasedForReturnReport(any());
        byte[] report = repairTicketService.ticketReleasedForReturnReport(filterDTO);
        assertNotNull(report);
    }

    @Test
    void ticketReleasedForReturnReportEmpty(){
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        TicketReleasedFilterDTO filterDTO = TicketReleasedFilterDTO.builder().initialOpenDate(initialDate).finalOpenDate(finalDate).initialCloseDate(initialDate).finalCloseDate(finalDate).build();
        List<TicketReleasedDTO> ticketReleasedDTOS = List.of();
        Mockito.doReturn(ticketReleasedDTOS).when(repairTicketRepository).getTicketReleased(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.ticketReleasedForReturnReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void ticketReleasedForReturnReportError() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        TicketReleasedFilterDTO filterDTO = TicketReleasedFilterDTO.builder().initialOpenDate(initialDate).finalOpenDate(finalDate).initialCloseDate(initialDate).finalCloseDate(finalDate).build();
        List<TicketReleasedDTO> ticketReleasedDTOS = new EasyRandom().objects(TicketReleasedDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(ticketReleasedDTOS).when(repairTicketRepository).getTicketReleased(any());
        Mockito.doThrow(JRException.class).when(reportService).ticketReleasedForReturnReport(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.ticketReleasedForReturnReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void ticketForwardedReport() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        TicketForwardedFilterDTO filterDTO = TicketForwardedFilterDTO.builder().initialDate(initialDate).finalDate(finalDate).build();
        List<TicketForwardedDTO> ticketForwardedDTOS = new EasyRandom().objects(TicketForwardedDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(ticketForwardedDTOS).when(repairTicketRepository).getTicketForwarded(any(), any());
        Mockito.doReturn(new byte[50]).when(reportService).ticketForwardedReport(any());
        byte[] report = repairTicketService.ticketForwardedReport(filterDTO);
        assertNotNull(report);
    }

    @Test
    void ticketForwardedReportEmpty()  {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        TicketForwardedFilterDTO filterDTO = TicketForwardedFilterDTO.builder().initialDate(initialDate).finalDate(finalDate).build();
        List<TicketForwardedDTO> ticketForwardedDTOS = List.of();
        Mockito.doReturn(ticketForwardedDTOS).when(repairTicketRepository).getTicketForwarded(any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.ticketForwardedReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void ticketForwardedReportError() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        TicketForwardedFilterDTO filterDTO = TicketForwardedFilterDTO.builder().initialDate(initialDate).finalDate(finalDate).build();
        List<TicketForwardedDTO> ticketForwardedDTOS = new EasyRandom().objects(TicketForwardedDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(ticketForwardedDTOS).when(repairTicketRepository).getTicketForwarded(any(), any());
        Mockito.doThrow(JRException.class).when(reportService).ticketForwardedReport(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.ticketForwardedReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void averageTimeReport() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        AverageTimeFilterDTO filterDTO = AverageTimeFilterDTO.builder().initialDate(initialDate).finalDate(finalDate).build();
        List<AverageTimeDTO> averageTimeDTOS =  new EasyRandom().objects(AverageTimeDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(averageTimeDTOS).when(repairTicketRepository).getAverageTime(any());
        Mockito.doReturn(new byte[50]).when(reportService).averageTimeRepairReport(any());
        byte[] report = repairTicketService.averageTimeReport(filterDTO);
        assertNotNull(report);
    }

    @Test
    void averageTimeReportEmpty() {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        AverageTimeFilterDTO filterDTO = AverageTimeFilterDTO.builder().initialDate(initialDate).finalDate(finalDate).build();
        List<AverageTimeDTO> averageTimeDTOS = List.of();
        Mockito.doReturn(averageTimeDTOS).when(repairTicketRepository).getAverageTime(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.averageTimeReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void averageTimeReportError() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        AverageTimeFilterDTO filterDTO = AverageTimeFilterDTO.builder().initialDate(initialDate).finalDate(finalDate).build();
        List<AverageTimeDTO> averageTimeDTOS = new EasyRandom().objects(AverageTimeDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(averageTimeDTOS).when(repairTicketRepository).getAverageTime(any());
        Mockito.doThrow(JRException.class).when(reportService).averageTimeRepairReport(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.averageTimeReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void summaryEquipmentReport() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        List<EquipamentTypeRepairDTO> equipmentTypeRepairDTOS =  new EasyRandom().objects(EquipamentTypeRepairDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(equipmentTypeRepairDTOS).when(equipmentTypeRepairRepository).findByRepairCenter(any(), any(), any());
        Mockito.doReturn(new byte[50]).when(reportService).summaryEquipmentReport(any());
        byte[] report = repairTicketService.summaryEquipmentReport("Test", initialDate, finalDate) ;
        assertNotNull(report);
    }

    @Test
    void summaryEquipmentReportEmpty() {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        List<EquipamentTypeRepairDTO> equipmentTypeRepairDTOS =  List.of();
        Mockito.doReturn(equipmentTypeRepairDTOS).when(equipmentTypeRepairRepository).findByRepairCenter(any(), any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.summaryEquipmentReport("Test", initialDate, finalDate));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void summaryEquipmentReportError() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        List<EquipamentTypeRepairDTO> equipmentTypeRepairDTOS =  new EasyRandom().objects(EquipamentTypeRepairDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(equipmentTypeRepairDTOS).when(equipmentTypeRepairRepository).findByRepairCenter(any(), any(), any());
        Mockito.doThrow(JRException.class).when(reportService).summaryEquipmentReport(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.summaryEquipmentReport("Test", initialDate, finalDate));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void openRepairReport() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        OpenRepairFilterDTO filterDTO = OpenRepairFilterDTO.builder().initialDate(initialDate).finalDate(finalDate).build();
        List<OpenRepairDTO> openRepairDTOS =  new EasyRandom().objects(OpenRepairDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(openRepairDTOS).when(repairTicketRepository).getOpenRepairs(any());
        Mockito.doReturn(new byte[50]).when(reportService).openRepairReport(any());
        byte[] report = repairTicketService.openRepairReport(filterDTO) ;
        assertNotNull(report);
    }

    @Test
    void openRepairReportEmpty() {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        OpenRepairFilterDTO filterDTO = OpenRepairFilterDTO.builder().initialDate(initialDate).finalDate(finalDate).build();
        List<OpenRepairDTO> openRepairDTOS =  List.of();
        Mockito.doReturn(openRepairDTOS).when(repairTicketRepository).getOpenRepairs(any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.openRepairReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void openRepairReportError() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        OpenRepairFilterDTO filterDTO = OpenRepairFilterDTO.builder().initialDate(initialDate).finalDate(finalDate).build();
        List<OpenRepairDTO> openRepairDTOS =  new EasyRandom().objects(OpenRepairDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(openRepairDTOS).when(repairTicketRepository).getOpenRepairs(any());
        Mockito.doThrow(JRException.class).when(reportService).openRepairReport(any());
        Exception e = assertThrows(IllegalArgumentException.class,  ()->repairTicketService.openRepairReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void costComparisonRepairReport() throws JRException, IOException {
        LocalDateTime initialDate = LocalDateTime.now();
        LocalDateTime finalDate = LocalDateTime.now().plusDays(5);
        CostComparisonRepairFilterDTO costComparisonRepairFilterDTO = CostComparisonRepairFilterDTO.builder().initialDate(initialDate)
                .finalDate(finalDate).detail(CostComparisonReportBreakEnum.UNIDADE).repairCenter("Teste").build();
        Mockito.doReturn(new byte[50]).when(costComparisonService).report(Mockito.any());
        byte[] report = repairTicketService.costComparisonRepairReport(costComparisonRepairFilterDTO) ;
        assertNotNull(report);
    }

    @Test
    void productivityComparisonReport() throws JRException, IOException {
        ProductivityComparisonFilterDTO filterDTO = ProductivityComparisonFilterDTO.builder().repairCenter("RJ-OI-ARC").technicalStaffName(null).initialDate(null).finalDate(null).build();
        List<ProductivityComparisonDTO> productivityComparisonDTO = new EasyRandom().objects(ProductivityComparisonDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(productivityComparisonDTO).when(productivityComparisonRepository).findProductivityComparisonByTechnical(anyString(), any(), any(), any());
        Mockito.doReturn(new byte[50]).when(reportService).productivityComparisonReport(any(), any());
        byte[] report = repairTicketService.productivityComparisonReport(filterDTO);
        assertNotNull(report);
    }

    @Test
    void productivityComparisonReportEmpty() throws JRException, IOException {
        ProductivityComparisonFilterDTO filterDTO = ProductivityComparisonFilterDTO.builder().repairCenter("RJ-OI-ARC").technicalStaffName(null).initialDate(null).finalDate(null).build();
        List<ProductivityComparisonDTO> productivityComparisonDTO = List.of();
        Mockito.doReturn(productivityComparisonDTO).when(productivityComparisonRepository).findProductivityComparisonByTechnical(any(), any(), any(), any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->repairTicketService.productivityComparisonReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void productivityComparisonReportError() throws JRException, IOException{
        ProductivityComparisonFilterDTO filterDTO = ProductivityComparisonFilterDTO.builder().repairCenter("RJ-OI-ARC").technicalStaffName(null).initialDate(null).finalDate(null).build();
        List<ProductivityComparisonDTO> productivityComparisonDTO = new EasyRandom().objects(ProductivityComparisonDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(productivityComparisonDTO).when(productivityComparisonRepository).findProductivityComparisonByTechnical(anyString(), any(), any(), any());
        Mockito.doThrow(JRException.class).when(reportService).productivityComparisonReport(any(), any());
        Exception e = assertThrows(IllegalArgumentException.class,  ()->repairTicketService.productivityComparisonReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }
}