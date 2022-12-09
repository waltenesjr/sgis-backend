package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Estimate;
import br.com.oi.sgis.entity.ItemEstimate;
import br.com.oi.sgis.exception.ItemEstimateNotFoundException;
import br.com.oi.sgis.repository.DepartmentRepository;
import br.com.oi.sgis.repository.EstimateRepository;
import br.com.oi.sgis.repository.ItemEstimateRepository;
import br.com.oi.sgis.util.MessageUtils;
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
class ItemEstimateServiceTest {

    @InjectMocks
    private ItemEstimateService itemEstimateService;
    @Mock
    private EstimateRepository estimateRepository;
    @Mock
    private ItemEstimateRepository itemEstimateRepository;
    @Mock
    private  ReportService reportService;
    @Mock
    private  NasphService nasphService;
    @Mock
    private DepartmentRepository departmentRepository;
    @Test
    void listAllPaginated() {
        List<ItemEstimate> itemEstimates = new EasyRandom().objects(ItemEstimate.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ItemEstimate> pagedResult = new PageImpl<>(itemEstimates, paging, itemEstimates.size());

        Mockito.doReturn(pagedResult).when(itemEstimateRepository).findLike(Mockito.any(), Mockito.any(),Mockito.any(Pageable.class));
        ItemEstimatesAnalysisDTO itemEstimatesAnalysisDTO = new EasyRandom().nextObject(ItemEstimatesAnalysisDTO.class);
        itemEstimatesAnalysisDTO.setInitialDate(LocalDateTime.now());
        itemEstimatesAnalysisDTO.setFinalDate(LocalDateTime.now().plusDays(5));
        PaginateResponseDTO<ItemEstimateDTO> itemEstimatesToReturn = itemEstimateService.listAllPaginated(0, 10, List.of("id"), List.of("date"), itemEstimatesAnalysisDTO );
        assertEquals(itemEstimates.size(), itemEstimatesToReturn.getData().size());
    }

    @Test
    void findById() throws ItemEstimateNotFoundException {
        ItemEstimate itemEstimate = new EasyRandom().nextObject(ItemEstimate.class);
        ItemEstimateDTO itemEstimateToFind = new EasyRandom().nextObject(ItemEstimateDTO.class);
        Mockito.doReturn(Optional.of(itemEstimate)).when(itemEstimateRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        ItemEstimateDTO itemEstimateDTO = itemEstimateService.findById(itemEstimateToFind);
        Assertions.assertEquals(itemEstimate.getId().getEstimate().getId(), itemEstimateDTO.getEstimate());
    }

    @Test
    void updateAnalysis() throws ItemEstimateNotFoundException {
        ItemEstimate itemEstimate = new EasyRandom().nextObject(ItemEstimate.class);
        ItemEstimateDTO itemEstimateDTO = new EasyRandom().nextObject(ItemEstimateDTO.class);
        Mockito.doReturn(Optional.of(itemEstimate)).when(itemEstimateRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());

        MessageResponseDTO response = itemEstimateService.updateAnalysis(List.of(itemEstimateDTO));
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateAnalysisException(){
        ItemEstimateDTO itemEstimateDTO = new EasyRandom().nextObject(ItemEstimateDTO.class);
        List<ItemEstimateDTO> items = List.of(itemEstimateDTO);
        Exception e = assertThrows(IllegalArgumentException.class, ()-> itemEstimateService.updateAnalysis(items));
        assertEquals(MessageUtils.ITEM_ESTIMATE_ANALYSIS_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void analysisReport() throws JRException, IOException {
        List<ItemEstimate> itemEstimates = new EasyRandom().objects(ItemEstimate.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ItemEstimate> pagedResult = new PageImpl<>(itemEstimates, paging, itemEstimates.size());
        ItemEstimatesAnalysisDTO itemEstimatesAnalysisDTO = new EasyRandom().nextObject(ItemEstimatesAnalysisDTO.class);
        itemEstimatesAnalysisDTO.setInitialDate(LocalDateTime.now());
        itemEstimatesAnalysisDTO.setFinalDate(LocalDateTime.now().plusDays(5));
        Department department = new EasyRandom().nextObject(Department.class);
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        byte[] report = new byte[50];
        Mockito.doReturn(Optional.of(department)).when(departmentRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(estimate)).when(estimateRepository).findById(Mockito.any());
        Mockito.doReturn(pagedResult).when(itemEstimateRepository).findLike(Mockito.any(), Mockito.any(),Mockito.any(Pageable.class));

        Mockito.doReturn(report).when(reportService).analysisItemEstimateReport(Mockito.any(), Mockito.any());
        byte[] returnedReport = itemEstimateService.analysisReport(itemEstimatesAnalysisDTO, List.of(), List.of());
        assertNotNull(returnedReport);
    }
    @Test
    void analysisReportEmpty()  {
        List<ItemEstimate> itemEstimates = List.of();
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ItemEstimate> pagedResult = new PageImpl<>(itemEstimates, paging, itemEstimates.size());
        Mockito.doReturn(pagedResult).when(itemEstimateRepository).findLike(Mockito.any(), Mockito.any(),Mockito.any(Pageable.class));

        ItemEstimatesAnalysisDTO itemEstimatesAnalysisDTO = new EasyRandom().nextObject(ItemEstimatesAnalysisDTO.class);
        itemEstimatesAnalysisDTO.setInitialDate(LocalDateTime.now());
        itemEstimatesAnalysisDTO.setFinalDate(LocalDateTime.now().plusDays(5));
        Exception e = assertThrows(IllegalArgumentException.class, () ->  itemEstimateService.analysisReport(itemEstimatesAnalysisDTO,null, null));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void updateCancelApprove() throws ItemEstimateNotFoundException {
        ItemEstimate itemEstimate = new EasyRandom().nextObject(ItemEstimate.class);
        ApproveCancelItemEstimateDTO approveCancelItemEstimateDTO = new EasyRandom().nextObject(ApproveCancelItemEstimateDTO.class);
        approveCancelItemEstimateDTO.setApprove(Boolean.TRUE);
        Mockito.doReturn(Optional.of(itemEstimate)).when(itemEstimateRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());

        MessageResponseDTO response = itemEstimateService.updateCancelApprove(List.of(approveCancelItemEstimateDTO));
        assertEquals(HttpStatus.OK, response.getStatus());
    }
    @Test
    void updateCancel() throws ItemEstimateNotFoundException {
        ItemEstimate itemEstimate = new EasyRandom().nextObject(ItemEstimate.class);
        ApproveCancelItemEstimateDTO approveCancelItemEstimateDTO = new EasyRandom().nextObject(ApproveCancelItemEstimateDTO.class);
        approveCancelItemEstimateDTO.setApprove(Boolean.FALSE);
        Mockito.doReturn(Optional.of(itemEstimate)).when(itemEstimateRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());

        MessageResponseDTO response = itemEstimateService.updateCancelApprove(List.of(approveCancelItemEstimateDTO));
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateCancelApproveException() {
        ItemEstimate itemEstimate = new EasyRandom().nextObject(ItemEstimate.class);
        ApproveCancelItemEstimateDTO approveCancelItemEstimateDTO = new EasyRandom().nextObject(ApproveCancelItemEstimateDTO.class);
        approveCancelItemEstimateDTO.setApprove(Boolean.TRUE);
        Mockito.doReturn(Optional.of(itemEstimate)).when(itemEstimateRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doThrow(IllegalArgumentException.class).when(nasphService).approveItemEstimate(Mockito.any(), Mockito.any(), Mockito.any());
        List<ApproveCancelItemEstimateDTO> approveCancelItemEstimateDTOS = List.of(approveCancelItemEstimateDTO);
        Exception e = assertThrows(IllegalArgumentException.class, ()->itemEstimateService.updateCancelApprove(approveCancelItemEstimateDTOS));
        assertEquals(MessageUtils.ITEM_ESTIMATE_CANCEL_CANCEL_ERROR.getDescription(), e.getMessage());
    }
}