package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.*;
import br.com.oi.sgis.exception.RepSituationNotFoundException;
import br.com.oi.sgis.exception.RepairTicketException;
import br.com.oi.sgis.service.RepairTicketService;
import br.com.oi.sgis.util.PageableUtil;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RepairTicketControllerTest {

    @InjectMocks
    private RepairTicketController repairTicketController;

    @Mock
    private RepairTicketService service;

    private final String MESSAGE_ERROR_PRODUCTIVITY_COMP = "Deve ser informada a área administrativa";

    @Test
    void unity() {
        UnityDTO unity = UnityDTO.builder().id("1").build();
        Mockito.doReturn(unity).when(service).getUnity(Mockito.any());

        UnityDTO returnedUnity = repairTicketController.unity("1");
        assertEquals(unity.getId(), returnedUnity.getId());

    }

    @Test
    void priorities() {
        List<PriorityRepairDTO> priorityRepairDTOList = repairTicketController.priorities();
        assertEquals(PriorityRepairEnum.values().length, priorityRepairDTOList.size());
    }

    @Test
    void repSituation() throws RepSituationNotFoundException {
        SituationDTO situationDTO = SituationDTO.builder().id("ABE").build();
        Mockito.doReturn(situationDTO).when(service).getDefaultRepSituation();
        SituationDTO returnedSituation = repairTicketController.repSituation();

        assertEquals(situationDTO.getId(), returnedSituation.getId());
    }

    @Test
    void repSituationWithException() throws RepSituationNotFoundException {
        Mockito.doThrow(RepSituationNotFoundException.class).when(service).getDefaultRepSituation();
        assertThrows(RepSituationNotFoundException.class, ()-> repairTicketController.repSituation());
    }

    @Test
    void fasNumber() {
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().message("Teste").title("Título").status(HttpStatus.OK).build();
        Mockito.doReturn(messageResponseDTO).when(service).fasNumber(Mockito.any());
        MessageResponseDTO response = repairTicketController.fasNumber("454");

        assertEquals(messageResponseDTO.getMessage(), response.getMessage());
        assertEquals(messageResponseDTO.getStatus(), response.getStatus());
        assertEquals(messageResponseDTO.getTitle(), response.getTitle());
    }

    @Test
    void createRepairTicket() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().message("Teste").title("Título").status(HttpStatus.CREATED).build();

        Mockito.doReturn(messageResponseDTO).when(service).createRepairTicket(Mockito.any());
        MessageResponseDTO response = repairTicketController.createRepairTicket(repairTicketDTO);

        assertEquals(messageResponseDTO.getMessage(), response.getMessage());
        assertEquals(messageResponseDTO.getStatus(), response.getStatus());
        assertEquals(messageResponseDTO.getTitle(), response.getTitle());
    }

    @Test
    void listAllWithSearch() {
        List<RepairTicketDTO> repairTicketDTOS = new EasyRandom().objects(RepairTicketDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(repairTicketDTOS, paging, repairTicketDTOS.size()));

        Mockito.doReturn(expectedResponse).when(service).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = repairTicketController.listAllWithSearch(0, 10,  List.of("brNumber"), null, "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void updateRepairTicket() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().message("Teste").title("Título").status(HttpStatus.OK).build();

        Mockito.doReturn(messageResponseDTO).when(service).updateRepairTicket(Mockito.any());
        MessageResponseDTO response = repairTicketController.updateRepairTicket(repairTicketDTO);

        assertEquals(messageResponseDTO.getMessage(), response.getMessage());
        assertEquals(messageResponseDTO.getStatus(), response.getStatus());
        assertEquals(messageResponseDTO.getTitle(), response.getTitle());
    }

    @Test
    void extraInformation() throws RepairTicketException {
        TicketRepExtraInfoDTO extraInfoDTO = new EasyRandom().nextObject(TicketRepExtraInfoDTO.class);
        Mockito.doReturn(extraInfoDTO).when(service).getExtraInformation(Mockito.any());

        TicketRepExtraInfoDTO returnedExtraInfo = repairTicketController.extraInformation("1L");
        assertEquals(extraInfoDTO.getAcceptDate(), returnedExtraInfo.getAcceptDate());
        assertEquals(extraInfoDTO.getTicketIntervention().size(), returnedExtraInfo.getTicketIntervention().size());

    }

    @Test
    void acceptUnity() {
        AcceptTicketRepairDTO acceptTicketRepairDTO = new EasyRandom().nextObject(AcceptTicketRepairDTO.class);
        Mockito.doReturn(acceptTicketRepairDTO).when(service).getAcceptRepair(Mockito.any());

        AcceptTicketRepairDTO returnedAcceptTicketRepairDTO = repairTicketController.acceptUnity("1L");
        assertEquals(acceptTicketRepairDTO.getStationDTO(), returnedAcceptTicketRepairDTO.getStationDTO());
        assertEquals(acceptTicketRepairDTO.getLocation(), returnedAcceptTicketRepairDTO.getLocation());
    }

    @Test
    void testAcceptUnity() {
        AcceptTicketRepairDTO acceptTicketRepairDTO = new EasyRandom().nextObject(AcceptTicketRepairDTO.class);
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().message("Teste").title("Título").status(HttpStatus.OK).build();

        Mockito.doReturn(messageResponseDTO).when(service).acceptRepair(Mockito.any());
        MessageResponseDTO response = repairTicketController.acceptUnity(acceptTicketRepairDTO);

        assertEquals(messageResponseDTO.getMessage(), response.getMessage());
        assertEquals(messageResponseDTO.getStatus(), response.getStatus());
        assertEquals(messageResponseDTO.getTitle(), response.getTitle());
    }

    @Test
    void verifyWarranty() {
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().message("Teste").title("Título").status(HttpStatus.OK).build();
        ResponseEntity<MessageResponseDTO> response = ResponseEntity.accepted().body(messageResponseDTO);
        Mockito.doReturn(response).when(service).verifyWarrantyDate(Mockito.any());

        ResponseEntity<MessageResponseDTO> returnedResponse = repairTicketController.verifyWarranty("1L");

        assertNotNull(returnedResponse.getBody());
        assertEquals(response.getBody().getMessage(), returnedResponse.getBody().getMessage());
        assertEquals(response.getStatusCode(), returnedResponse.getStatusCode());
    }

    @Test
    void findById() throws RepairTicketException {
        RepairTicketDTO repairTicket = new EasyRandom().nextObject(RepairTicketDTO.class);
        Mockito.doReturn(repairTicket).when(service).findById(Mockito.any());

        RepairTicketDTO repairTicketDTO = repairTicketController.findById("1233");
        assertNotNull(repairTicketDTO);
        assertEquals(repairTicket.getBrNumber(), repairTicketDTO.getBrNumber());
    }

    @Test
    void forwardRepair() {
        List<RepairTicketDTO> repairTicketDTOS = new EasyRandom().objects(RepairTicketDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(repairTicketDTOS, paging, repairTicketDTOS.size()));

        Mockito.doReturn(expectedResponse).when(service).getForwardRepair(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ForwardTicketDTO dto = new EasyRandom().nextObject(ForwardTicketDTO.class);
        PaginateResponseDTO<RepairTicketDTO> response = repairTicketController.forwardRepair(dto, 1, 10, List.of(), List.of());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getData().get(0));
    }

    @Test
    void testForwardRepair() {
        RepairTicketDTO dto = new EasyRandom().nextObject(RepairTicketDTO.class);
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().message("Teste").title("Título").status(HttpStatus.OK).build();

        Mockito.doReturn(messageResponseDTO).when(service).forwardRepair(Mockito.any());
        MessageResponseDTO response = repairTicketController.forwardRepair(dto);

        assertEquals(messageResponseDTO.getMessage(), response.getMessage());
    }

    @Test
    void testForwardRepair1() throws RepairTicketException {
        RepairTicketDTO dto = new EasyRandom().nextObject(RepairTicketDTO.class);
        Mockito.doReturn(dto).when(service).ticketToForward(Mockito.any());
        RepairTicketDTO response = repairTicketController.forwardRepair("126");
        assertEquals(dto.getBrNumber(), response.getBrNumber());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        ForwardTicketDTO dto = new EasyRandom().nextObject(ForwardTicketDTO.class);

        Mockito.doReturn(report).when(service).forwardTicketReport(Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.forwardTicketReport(List.of(), List.of(),dto);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void closeRepair() {
        CloseRepairTickectDTO dto = new EasyRandom().nextObject(CloseRepairTickectDTO.class);
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().message("Teste").title("Título").status(HttpStatus.OK).build();
        Mockito.doReturn(messageResponseDTO).when(service).closeRepair(Mockito.any());
        MessageResponseDTO response = repairTicketController.closeRepair(dto);
        assertEquals(messageResponseDTO.getMessage(), response.getMessage());
    }

    @Test
    void closeRepairSituation() throws RepSituationNotFoundException {
        SituationDTO situationDTO = new EasyRandom().nextObject(SituationDTO.class);
        Mockito.doReturn(situationDTO).when(service).closeRepairSituation(Mockito.any());
        SituationDTO response = repairTicketController.closeRepairSituation("1");
        assertEquals(situationDTO.getId(), response.getId());
    }

    @Test
    void devolutionRepair() {
        DevolutionRepairTicketDTO dto = new EasyRandom().nextObject(DevolutionRepairTicketDTO.class);
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().message("Teste").title("Título").status(HttpStatus.OK).build();
        Mockito.doReturn(messageResponseDTO).when(service).devolutionRepair(Mockito.any());
        MessageResponseDTO response = repairTicketController.devolutionRepair(dto);
        assertEquals(messageResponseDTO.getMessage(), response.getMessage());
    }

    @Test
    void cancelRepair() {
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().message("Teste").title("Título").status(HttpStatus.OK).build();
        Mockito.doReturn(messageResponseDTO).when(service).cancelRepair(Mockito.any());
        MessageResponseDTO response = repairTicketController.cancelRepair("154");
        assertEquals(messageResponseDTO.getMessage(), response.getMessage());
    }

    @Test
    void getDesignateTechnician() {
        List<RepairTicketDTO> repairTicketDTOS = new EasyRandom().objects(RepairTicketDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(repairTicketDTOS, paging, repairTicketDTOS.size()));

        Mockito.doReturn(expectedResponse).when(service).getDesignateTechnician(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        DesignateTechnicianDTO dto = new EasyRandom().nextObject(DesignateTechnicianDTO.class);
        PaginateResponseDTO<RepairTicketDTO> response = repairTicketController.getDesignateTechnician(dto, 1, 10, List.of(), List.of());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getData().get(0));
    }

    @Test
    void designateTechnician() {
        RepairTicketDTO dto = new EasyRandom().nextObject(RepairTicketDTO.class);
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().message("Teste").title("Título").status(HttpStatus.OK).build();

        Mockito.doReturn(messageResponseDTO).when(service).designateTechnician(Mockito.any());
        MessageResponseDTO response = repairTicketController.designateTechnician(dto);

        assertEquals(messageResponseDTO.getMessage(), response.getMessage());
    }

    @Test
    void listAllForOpenIntervention() {
        List<RepairTicketDTO> repairTicketDTOS = new EasyRandom().objects(RepairTicketDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(repairTicketDTOS, paging, repairTicketDTOS.size()));

        Mockito.doReturn(expectedResponse).when(service).getTicketsForIntervention(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<RepairTicketDTO>>response = repairTicketController.listAllForOpenIntervention(1, 10,  List.of(), List.of(), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }
    @Test
    void summaryReport() throws JRException, IOException {
        byte[] report = new byte[50];
        RepairSummaryFilterDTO dto = new EasyRandom().nextObject(RepairSummaryFilterDTO.class);
        Mockito.doReturn(report).when(service).repairSummaryReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.summaryReport(dto);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void analyticReport() {
        byte[] report = new byte[50];
        RepairAnalyticFilterDTO dto = new EasyRandom().nextObject(RepairAnalyticFilterDTO.class);
        Mockito.doReturn(report).when(service).analyticReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.analyticReport(dto);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void testAnalyticReport() {
        List<ReporterOrderDTO> listReport = repairTicketController.analyticReport();
        assertEquals(BreaksAnalyticRepairEnum.values().length, listReport.size());
    }

    @Test
    void externalRepairReport() {
        byte[] report = new byte[50];
        ExternalRepairFilterDTO dto = new EasyRandom().nextObject(ExternalRepairFilterDTO.class);
        Mockito.doReturn(report).when(service).externalRepairReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.externalRepairReport(dto);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void operatorTicketReport() {
        byte[] report = new byte[50];
        OperatorTicketFilterDTO dto = new EasyRandom().nextObject(OperatorTicketFilterDTO.class);
        Mockito.doReturn(report).when(service).operatorTicketReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.operatorTicketReport(dto);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void ticketReleasedForReturnReport() {
        byte[] report = new byte[50];
        TicketReleasedFilterDTO dto = new EasyRandom().nextObject(TicketReleasedFilterDTO.class);
        Mockito.doReturn(report).when(service).ticketReleasedForReturnReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.ticketReleasedForReturnReport(dto);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void conditionsReleasedReport() {
        List<ReporterOrderDTO> listReport = repairTicketController.conditionsReleasedReport();
        assertEquals(ConditionsTicketReleasedEnum.values().length, listReport.size());
    }

    @Test
    void ticketForwardedReport() {
        byte[] report = new byte[50];
        TicketForwardedFilterDTO dto = new EasyRandom().nextObject(TicketForwardedFilterDTO.class);
        Mockito.doReturn(report).when(service).ticketForwardedReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.ticketForwardedReport(dto);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void conditionsForwardedReport() {
        List<ReporterOrderDTO> listReport = repairTicketController.conditionsForwardedReport();
        assertEquals(ConditionsTicketForwardedEnum.values().length, listReport.size());
    }

    @Test
    void averageTimeReport() {
        byte[] report = new byte[50];
        AverageTimeFilterDTO dto = new EasyRandom().nextObject(AverageTimeFilterDTO.class);
        Mockito.doReturn(report).when(service).averageTimeReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.averageTimeReport(dto);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void summaryEquipmentReport() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(service).summaryEquipmentReport(Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.summaryEquipmentReport("Test", LocalDateTime.now(), LocalDateTime.now());
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void openRepairReport() {
        byte[] report = new byte[50];
        OpenRepairFilterDTO dto = new EasyRandom().nextObject(OpenRepairFilterDTO.class);
        Mockito.doReturn(report).when(service).openRepairReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.openRepairReport(dto);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void costComparisonRepairReport() throws JRException, IOException {
        byte[] report = new byte[50];
        CostComparisonRepairFilterDTO dto = new EasyRandom().nextObject(CostComparisonRepairFilterDTO.class);
        Mockito.doReturn(report).when(service).costComparisonRepairReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.costComparisonRepairReport(dto);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void costComparisonRepairReportDetails() {
        List<ReporterOrderDTO> listReport = repairTicketController.costComparisonRepairReportDetails();
        assertEquals(CostComparisonReportBreakEnum.values().length, listReport.size());
    }

    @Test
    void productivityComparisonReport() {
        byte[] report = new byte[50];
        ProductivityComparisonFilterDTO filterDTO = new EasyRandom().nextObject(ProductivityComparisonFilterDTO.class);
        Mockito.doReturn(report).when(service).productivityComparisonReport(Mockito.any());
        ResponseEntity<byte[]> responseReport = repairTicketController.productivityComparisonReport(filterDTO);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

}