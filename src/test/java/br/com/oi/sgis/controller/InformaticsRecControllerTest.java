package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.SapOperationHistoryDTO;
import br.com.oi.sgis.dto.SapOperationHistoryReportDTO;
import br.com.oi.sgis.dto.SituationSapDTO;
import br.com.oi.sgis.enums.SituationSapEnum;
import br.com.oi.sgis.service.InformaticsRecService;
import br.com.oi.sgis.util.ExceptionHandlerAdvice;
import br.com.oi.sgis.util.PageableUtil;
import org.jeasy.random.EasyRandom;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ExceptionHandlerAdvice.class)
public class InformaticsRecControllerTest {

    @InjectMocks
    private InformaticsRecController informaticsRecController;

    @Mock
    private InformaticsRecService informaticsRecService;

    @Test
    void getSapOperationHistory() throws Exception {
        List<SapOperationHistoryReportDTO> sapOperationHistoryReportDTOs = new EasyRandom().objects(SapOperationHistoryReportDTO.class, 5).collect(Collectors.toList());
        SapOperationHistoryDTO sapOperationHistoryDTO = new EasyRandom().nextObject(SapOperationHistoryDTO.class);
        List<Sort.Order> orders = List.of(Sort.Order.asc("unity"), Sort.Order.desc("processingDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(sapOperationHistoryReportDTOs, paging, sapOperationHistoryReportDTOs.size()));

        Mockito.doReturn(expectedResponse).when(informaticsRecService).getSapOperationHistory(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(), Mockito.anyList());
        ResponseEntity<PaginateResponseDTO<SapOperationHistoryReportDTO>> response =
                informaticsRecController.getSapOperationHistory(sapOperationHistoryDTO, 0, 10, List.of("unity"), List.of());

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void situations(){
        List<SituationSapDTO> situationSapDTOs = informaticsRecController.situations();
        assertEquals(SituationSapEnum.values().length, situationSapDTOs.size());
    }
}
