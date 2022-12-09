package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Estimate;
import br.com.oi.sgis.exception.EstimateNotFoundException;
import br.com.oi.sgis.mapper.EstimateMapper;
import br.com.oi.sgis.repository.EstimateRepository;
import br.com.oi.sgis.repository.ItemEstimateRepository;
import br.com.oi.sgis.repository.TicketInterventionRepository;
import br.com.oi.sgis.service.factory.EstimateFactory;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EstimateServiceTest {

    @InjectMocks
    private EstimateService estimateService;
    @Mock
    private  EstimateRepository estimateRepository;
    @Mock
    private  ItemEstimateRepository itemEstimateRepository;
    @Mock
    private  ReportService reportService;
    @Mock
    private  EstimateFactory estimateFactory;
    @Mock
    private  Validator<EstimateDTO> validator;
    @Mock
    private TicketInterventionRepository ticketInterventionRepository;
    @Mock
    private NasphService nasphService;

    @Test
    void listAllPaginated() {
        List<Estimate> estimates = new EasyRandom().objects(Estimate.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Estimate> pagedResult = new PageImpl<>(estimates, paging, estimates.size());

        Mockito.doReturn(pagedResult).when(estimateRepository).findLike(Mockito.any(),Mockito.any(Pageable.class));
        PaginateResponseDTO<EstimateDTO> estimatesToReturn = estimateService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(estimates.size(), estimatesToReturn.getData().size());
    }

    @Test
    void listAllOpenPaginated() {
        List<Estimate> estimates = new EasyRandom().objects(Estimate.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Estimate> pagedResult = new PageImpl<>(estimates, paging, estimates.size());

        Mockito.doReturn(pagedResult).when(estimateRepository).findLikeOpen(Mockito.any(),Mockito.any(Pageable.class));
        PaginateResponseDTO<EstimateDTO> estimatesToReturn = estimateService.listAllOpenPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(estimates.size(), estimatesToReturn.getData().size());
    }

    @Test
    void findById() throws EstimateNotFoundException {
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        EstimateDTO estimateDTO = estimateService.findById(estimate.getId());
        Assertions.assertEquals(estimate.getId(), estimateDTO.getId());
    }

    @Test
    void findByIdException() {
        Mockito.doReturn(Optional.empty()).when(estimateRepository).findById(Mockito.any());
        Assertions.assertThrows(EstimateNotFoundException.class, () -> estimateService.findById("1"));
    }

    @Test
    void createEstimate() {
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        EstimateDTO estimateDTO = new EasyRandom().nextObject(EstimateDTO.class);
        Mockito.doReturn(estimate).when(estimateFactory).createEstimate(Mockito.any());
        MessageResponseDTO responseDTO = estimateService.createEstimate(estimateDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void estimateReport() throws JRException, IOException {
        List<Estimate> estimates = new EasyRandom().objects(Estimate.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Estimate> pagedResult = new PageImpl<>(estimates, paging, estimates.size());
        List<EstimateItemReportDTO> items = new EasyRandom().objects(EstimateItemReportDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(pagedResult).when(estimateRepository).findLike(Mockito.any(),Mockito.any(Pageable.class));
        Mockito.doReturn(items).when(itemEstimateRepository).estimateItemReport(Mockito.any());
        byte[] report = new byte [50];
        Mockito.doReturn(report).when(reportService).estimateReport(Mockito.any(), Mockito.any());
        byte[] returnedReport = estimateService.estimateReport("12", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void estimateReportException(){
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Estimate> pagedResult = new PageImpl<>(List.of(), paging, 0);
        Mockito.doReturn(pagedResult).when(estimateRepository).findLike(Mockito.any(),Mockito.any(Pageable.class));

        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () ->estimateService.estimateReport("12", null, null));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void updateEstimate() throws EstimateNotFoundException {
        EstimateMapper mapper = EstimateMapper.INSTANCE;
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        estimate.setSendDate(null);
        EstimateDTO estimateDTO = mapper.toDTO(estimate);
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        MessageResponseDTO responseDTO = estimateService.updateEstimate(estimateDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void updateEstimateRemoveItems() throws EstimateNotFoundException {
        EstimateMapper mapper = EstimateMapper.INSTANCE;
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        estimate.setSendDate(null);
        EstimateDTO estimateDTO = mapper.toDTO(estimate);
        estimateDTO.setItemEstimates(List.of());
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        MessageResponseDTO responseDTO = estimateService.updateEstimate(estimateDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void updateEstimateCreateItems() throws EstimateNotFoundException {
        EstimateMapper mapper = EstimateMapper.INSTANCE;
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        estimate.setSendDate(null);
        EstimateDTO estimateDTO = mapper.toDTO(estimate);
        estimate.setItemEstimates(List.of());
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        MessageResponseDTO responseDTO = estimateService.updateEstimate(estimateDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void updateEstimateExceptionSendDate() {
        EstimateMapper mapper = EstimateMapper.INSTANCE;
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        EstimateDTO estimateDTO = mapper.toDTO(estimate);
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        Exception e = assertThrows(IllegalArgumentException.class, ()->estimateService.updateEstimate(estimateDTO));
        assertEquals(MessageUtils.ESTIMATE_EXTERNAL_REP_SEND_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void externalOutput() throws EstimateNotFoundException {
        MessageResponseDTO returnResponse = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        EstimateExternalOutputDTO estimateExternalOutputDTO = new EasyRandom().nextObject(EstimateExternalOutputDTO.class);
        estimateExternalOutputDTO.setFiscalNoteDate(LocalDateTime.MIN);
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        estimate.setSendDate(null);
        String userDepartment = Utils.getUser().getDepartmentCode().getId();
        estimate.setDepartment(Department.builder().id(userDepartment).build());
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        Mockito.doReturn(returnResponse).when(nasphService).estimateExternalOutput(Mockito.any());
        MessageResponseDTO response = estimateService.externalOutput(estimateExternalOutputDTO);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void externalOutputDepartmentException() {
        EstimateExternalOutputDTO estimateExternalOutputDTO = new EasyRandom().nextObject(EstimateExternalOutputDTO.class);
        estimateExternalOutputDTO.setFiscalNoteDate(LocalDateTime.MIN);
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        estimate.setSendDate(null);
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        Exception e = assertThrows(IllegalArgumentException.class, ()-> estimateService.externalOutput(estimateExternalOutputDTO));
        assertEquals(MessageUtils.ESTIMATE_EXTERNAL_REP_DEPARTMENT_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void externalOutputSendException() {
        EstimateExternalOutputDTO estimateExternalOutputDTO = new EasyRandom().nextObject(EstimateExternalOutputDTO.class);
        estimateExternalOutputDTO.setFiscalNoteDate(LocalDateTime.MIN);
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        Exception e = assertThrows(IllegalArgumentException.class, ()-> estimateService.externalOutput(estimateExternalOutputDTO));
        assertEquals(MessageUtils.ESTIMATE_EXTERNAL_REP_SEND_OUT_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void externalOutputDateNFException() {
        EstimateExternalOutputDTO estimateExternalOutputDTO = new EasyRandom().nextObject(EstimateExternalOutputDTO.class);
        estimateExternalOutputDTO.setFiscalNoteDate(LocalDateTime.now().plusDays(5));
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        Exception e = assertThrows(IllegalArgumentException.class, ()-> estimateService.externalOutput(estimateExternalOutputDTO));
        assertEquals(MessageUtils.ESTIMATE_EXTERNAL_REP_DATE_NF_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void listAllSentPaginated() {
        List<Estimate> estimates = new EasyRandom().objects(Estimate.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Estimate> pagedResult = new PageImpl<>(estimates, paging, estimates.size());

        Mockito.doReturn(pagedResult).when(estimateRepository).findLikeSent(Mockito.any(),Mockito.any(Pageable.class));
        PaginateResponseDTO<EstimateDTO> estimatesToReturn = estimateService.listAllSentPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(estimates.size(), estimatesToReturn.getData().size());
    }

    @Test
    void estimateSentReport() throws JRException, IOException {
        List<Estimate> estimates = new EasyRandom().objects(Estimate.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Estimate> pagedResult = new PageImpl<>(estimates, paging, estimates.size());
        List<EstimateItemReportDTO> items = new EasyRandom().objects(EstimateItemReportDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(pagedResult).when(estimateRepository).findLikeSent(Mockito.any(),Mockito.any(Pageable.class));
        Mockito.doReturn(items).when(itemEstimateRepository).estimateItemReport(Mockito.any());
        byte[] report = new byte [50];
        Mockito.doReturn(report).when(reportService).estimateReport(Mockito.any(), Mockito.any());
        byte[] returnedReport = estimateService.estimateSentReport("12", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void updateSentEstimate() throws EstimateNotFoundException {
        EstimateMapper mapper = EstimateMapper.INSTANCE;
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        String userDepartment = Utils.getUser().getDepartmentCode().getId();
        estimate.setDepartment(Department.builder().id(userDepartment).build());
        estimate.setFiscalNoteDate(LocalDateTime.MIN);
        EstimateDTO estimateDTO = mapper.toDTO(estimate);
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        MessageResponseDTO responseDTO = estimateService.updateSentEstimate(estimateDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void updateSentEstimateNFDateException() throws EstimateNotFoundException {
        EstimateMapper mapper = EstimateMapper.INSTANCE;
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        String userDepartment = Utils.getUser().getDepartmentCode().getId();
        estimate.setDepartment(Department.builder().id(userDepartment).build());
        estimate.setFiscalNoteDate(LocalDateTime.now().plusDays(5));
        EstimateDTO estimateDTO = mapper.toDTO(estimate);
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        Exception e = assertThrows(IllegalArgumentException.class, ()-> estimateService.updateSentEstimate(estimateDTO));
        assertEquals(MessageUtils.ESTIMATE_EXTERNAL_REP_DATE_NF_ERROR.getDescription(), e.getMessage());
    }
    @Test
    void updateSentEstimateSendDateException()  {
        EstimateMapper mapper = EstimateMapper.INSTANCE;
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        estimate.setSendDate(null);
        String userDepartment = Utils.getUser().getDepartmentCode().getId();
        estimate.setDepartment(Department.builder().id(userDepartment).build());
        estimate.setFiscalNoteDate(LocalDateTime.MIN);
        EstimateDTO estimateDTO = mapper.toDTO(estimate);
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        Exception e = assertThrows(IllegalArgumentException.class, ()-> estimateService.updateSentEstimate(estimateDTO));
        assertEquals(MessageUtils.ESTIMATE_NOT_SENT_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void cancelEstimate() throws EstimateNotFoundException {
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        MessageResponseDTO returnResponse = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.anyString());
        Mockito.doReturn(returnResponse).when(nasphService).cancelEstimate(Mockito.any());
        MessageResponseDTO responseDTO = estimateService.cancelEstimate("123");
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void returnRepair() {
        RepairExternalReturnDTO repairExternalReturnDTO = new EasyRandom().nextObject(RepairExternalReturnDTO.class);
        repairExternalReturnDTO.setArrivalDate(LocalDateTime.now());
        repairExternalReturnDTO.setSubstitution(false);
        repairExternalReturnDTO.setFiscalNoteDate(LocalDateTime.now().minusDays(5));
        MessageResponseDTO returnResponse = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(returnResponse).when(nasphService).returnExternalRepair(Mockito.any());
        MessageResponseDTO responseDTO = estimateService.returnRepair(repairExternalReturnDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void returnRepairArrivalException() {
        RepairExternalReturnDTO repairExternalReturnDTO = new EasyRandom().nextObject(RepairExternalReturnDTO.class);
        repairExternalReturnDTO.setArrivalDate(LocalDateTime.now().plusDays(5));
        repairExternalReturnDTO.setSubstitution(false);
        repairExternalReturnDTO.setFiscalNoteDate(LocalDateTime.now().minusDays(5));
        Exception e = assertThrows(IllegalArgumentException.class, ()-> estimateService.returnRepair(repairExternalReturnDTO));
        assertEquals(MessageUtils.EXTERNAL_REPAIR_RETURN_ARRIVAL_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void returnRepairFiscalNoteDateException() {
        RepairExternalReturnDTO repairExternalReturnDTO = new EasyRandom().nextObject(RepairExternalReturnDTO.class);
        repairExternalReturnDTO.setArrivalDate(LocalDateTime.now());
        repairExternalReturnDTO.setSubstitution(false);
        repairExternalReturnDTO.setFiscalNoteDate(LocalDateTime.now().plusDays(5));
        Exception e = assertThrows(IllegalArgumentException.class, ()-> estimateService.returnRepair(repairExternalReturnDTO));
        assertEquals(MessageUtils.EXTERNAL_REPAIR_RETURN_NF_DATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void returnRepairSubstitutionException() {
        RepairExternalReturnDTO repairExternalReturnDTO = new EasyRandom().nextObject(RepairExternalReturnDTO.class);
        repairExternalReturnDTO.setArrivalDate(LocalDateTime.now());
        repairExternalReturnDTO.setSubstitution(true);
        repairExternalReturnDTO.setNewBarcode(null);
        repairExternalReturnDTO.setFiscalNoteDate(LocalDateTime.now().minusDays(5));
        Exception e = assertThrows(IllegalArgumentException.class, ()-> estimateService.returnRepair(repairExternalReturnDTO));
        assertEquals(MessageUtils.EXTERNAL_REPAIR_RETURN_SUBSTITUTION_ERROR.getDescription(), e.getMessage());
    }
}