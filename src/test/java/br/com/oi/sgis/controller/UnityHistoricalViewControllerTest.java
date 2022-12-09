package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.UnityHistoricalViewDTO;
import br.com.oi.sgis.dto.UnityHistoricalViewFilterDTO;
import br.com.oi.sgis.entity.view.UnityHistoricalView;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.service.UnityHistoricalViewService;
import br.com.oi.sgis.util.PageableUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UnityHistoricalViewControllerTest {

    @InjectMocks
    UnityHistoricalViewController unityHistoricalController;

    @Mock
    UnityHistoricalViewService unityHistoricalService;

    @Test
    void shouldFindAllUnityHistoricalWithoutFilterPaginated() {
        UnityHistoricalViewFilterDTO filterDTO = new EasyRandom().nextObject(UnityHistoricalViewFilterDTO.class);
        List<UnityHistoricalViewDTO> unitiesHistoricalDTO =
                new EasyRandom().objects(UnityHistoricalViewDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("initialDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<UnityHistoricalViewDTO> expectedResponse =
                PageableUtil.paginate(new PageImpl(unitiesHistoricalDTO, paging, unitiesHistoricalDTO.size()));


        doReturn(expectedResponse).when(unityHistoricalService)
                .findPaginated(any(), anyInt(), anyInt(), anyList(), anyList());

        ResponseEntity<PaginateResponseDTO<UnityHistoricalViewDTO>> response =
                unityHistoricalController.findPaginated(0, 10, List.of(), List.of(), filterDTO);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void shouldFindAllUnityHistoricalWithFilterPaginated() {
        UnityHistoricalViewFilterDTO filterDTO = new EasyRandom().nextObject(UnityHistoricalViewFilterDTO.class);
        filterDTO.setInitialDate(LocalDateTime.now());
        filterDTO.setFinalDate(LocalDateTime.now().plusDays(1));
        filterDTO.setBarcode("0123456789");
        List<UnityHistoricalViewDTO> unitiesHistoricalDTO =
                new EasyRandom().objects(UnityHistoricalViewDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("initialDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<UnityHistoricalViewDTO> expectedResponse =
                PageableUtil.paginate(new PageImpl(unitiesHistoricalDTO, paging, unitiesHistoricalDTO.size()));


        doReturn(expectedResponse).when(unityHistoricalService)
                .findPaginated(any(), anyInt(), anyInt(), anyList(), anyList());

        ResponseEntity<PaginateResponseDTO<UnityHistoricalViewDTO>> response =
                unityHistoricalController.findPaginated(0, 10, List.of(), List.of(), filterDTO);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void shouldReturnUnityHistoricalReport() {
        UnityHistoricalViewFilterDTO filterDTO = new EasyRandom().nextObject(UnityHistoricalViewFilterDTO.class);

        byte[] report = new byte[50];
        doReturn(report).when(unityHistoricalService).unityHistoricalReport(any(), anyList(), anyList(), any());

        ResponseEntity<byte[]> responseReport =
                unityHistoricalController.unityHistoricalReport(List.of(), List.of(), filterDTO, TypeDocEnum.XLSX);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }
}