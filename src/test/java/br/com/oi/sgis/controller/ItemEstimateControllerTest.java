package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.ItemEstimateNotFoundException;
import br.com.oi.sgis.service.ItemEstimateService;
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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
class ItemEstimateControllerTest {

    @InjectMocks
    private ItemEstimateController itemEstimateController;
    @Mock
    private ItemEstimateService itemEstimateService;
    @Test
    void listAllWithSearch() {
        List<ItemEstimateDTO> itemEstimateDTOS = new EasyRandom().objects(ItemEstimateDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(itemEstimateDTOS, paging, itemEstimateDTOS.size()));
        ItemEstimatesAnalysisDTO itemEstimatesAnalysisDTO = new EasyRandom().nextObject(ItemEstimatesAnalysisDTO.class);

        Mockito.doReturn(expectedResponse).when(itemEstimateService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ItemEstimateDTO>> response = itemEstimateController.listAllWithSearch(itemEstimatesAnalysisDTO, 0, 10,  List.of("id"), List.of("description"));

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void updateAnalysis() throws ItemEstimateNotFoundException {
        ItemEstimateDTO itemEstimateDTO = new EasyRandom().nextObject(ItemEstimateDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(itemEstimateService).updateAnalysis(Mockito.any());
        MessageResponseDTO returnedResponse = itemEstimateController.updateAnalysis(List.of(itemEstimateDTO));
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void analysisReport() throws JRException, IOException {
        byte[] report = new byte[50];
        ItemEstimatesAnalysisDTO itemEstimatesAnalysisDTO = new EasyRandom().nextObject(ItemEstimatesAnalysisDTO.class);
        Mockito.doReturn(report).when(itemEstimateService).analysisReport(Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<byte[]> responseReport = itemEstimateController.analysisReport(itemEstimatesAnalysisDTO, List.of(), List.of());

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void updateCancelApprove() throws ItemEstimateNotFoundException {
        ApproveCancelItemEstimateDTO approveCancelItemEstimateDTO = new EasyRandom().nextObject(ApproveCancelItemEstimateDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(itemEstimateService).updateCancelApprove(Mockito.any());
        MessageResponseDTO returnedResponse = itemEstimateController.updateCancelApprove(List.of(approveCancelItemEstimateDTO));
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

}