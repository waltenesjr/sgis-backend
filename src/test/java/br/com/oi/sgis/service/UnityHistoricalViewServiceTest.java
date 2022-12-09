package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.UnityHistoricalViewDTO;
import br.com.oi.sgis.dto.UnityHistoricalViewFilterDTO;
import br.com.oi.sgis.entity.view.UnityHistoricalView;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.repository.UnityHistoricalViewRepository;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.oi.sgis.util.MessageUtils.INVALID_PERIOD;
import static br.com.oi.sgis.util.MessageUtils.PERIOD_NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnityHistoricalViewServiceTest {

    @InjectMocks
    UnityHistoricalViewService unityHistoricalService;

    @Mock
    UnityHistoricalViewRepository unityHistoricalRepository;

    @Mock
    ReportService reportService;

    @Test
    void shouldFindAllWithoutFilter() {
        List<UnityHistoricalView> unitiesHistorical =
                new EasyRandom().objects(UnityHistoricalView.class, 5).collect(Collectors.toList());
        UnityHistoricalViewFilterDTO filterDTO = new EasyRandom().nextObject(UnityHistoricalViewFilterDTO.class);
        filterDTO.setInitialDate(null);
        filterDTO.setFinalDate(null);
        List<Sort.Order> orders = List.of(Sort.Order.asc("initialDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<UnityHistoricalView> pagedResult = new PageImpl<>(unitiesHistorical, paging, unitiesHistorical.size());

        doReturn(pagedResult).when(unityHistoricalRepository)
                .find(anyString(), anyString(), anyString(), anyString(), any(Pageable.class));

        PaginateResponseDTO<UnityHistoricalViewDTO> unitiesReturn =
                unityHistoricalService.findPaginated(filterDTO, 0, 10, List.of(), List.of("date"));

        assertEquals(pagedResult.getTotalElements(), unitiesReturn.getData().size());
    }

    @Test
    void shouldFindAllWithFilterDateAndBarcode() {
        List<UnityHistoricalView> unitiesHistorical =
                new EasyRandom().objects(UnityHistoricalView.class, 5).collect(Collectors.toList());
        UnityHistoricalViewFilterDTO filterDTO = new EasyRandom().nextObject(UnityHistoricalViewFilterDTO.class);
        filterDTO.setInitialDate(LocalDateTime.now());
        filterDTO.setFinalDate(LocalDateTime.now().plusDays(1));
        filterDTO.setBarcode("0123456789");
        List<Sort.Order> orders = List.of(Sort.Order.asc("initialDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<UnityHistoricalView> pagedResult = new PageImpl<>(unitiesHistorical, paging, unitiesHistorical.size());

        doReturn(pagedResult).when(unityHistoricalRepository)
                .findWithDate(anyString(), any(), any(), anyString(), anyString(), anyString(), any(Pageable.class));

        PaginateResponseDTO<UnityHistoricalViewDTO> unitiesReturn =
                unityHistoricalService.findPaginated(filterDTO, 0, 10, List.of(), List.of("date"));

        assertEquals(pagedResult.getTotalElements(), unitiesReturn.getData().size());
    }

    @Test
    void shouldThrowExceptionInvalidPeriodWhenFindAll() {
        UnityHistoricalViewFilterDTO filterDTO = new EasyRandom().nextObject(UnityHistoricalViewFilterDTO.class);
        filterDTO.setInitialDate(null);

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> unityHistoricalService.findPaginated(filterDTO, 0, 10, List.of(), List.of()));

        assertEquals(PERIOD_NULL.getDescription(), e.getMessage());
    }

    @Test
    void shouldThrowExceptionPeriodNullWhenFindAll() {
        UnityHistoricalViewFilterDTO filterDTO = new EasyRandom().nextObject(UnityHistoricalViewFilterDTO.class);
        filterDTO.setFinalDate(LocalDateTime.now());
        filterDTO.setInitialDate(LocalDateTime.now().plusDays(1));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> unityHistoricalService.findPaginated(filterDTO, 0, 10, List.of(), List.of()));

        assertEquals(INVALID_PERIOD.getDescription(), e.getMessage());
    }

    @Test
    void shouldReturnUnityHistoricalReport() throws JRException, IOException {
        List<UnityHistoricalView> unitiesHistorical =
                new EasyRandom().objects(UnityHistoricalView.class, 5).collect(Collectors.toList());
        UnityHistoricalViewFilterDTO filterDTO = new EasyRandom().nextObject(UnityHistoricalViewFilterDTO.class);
        filterDTO.setInitialDate(null);
        filterDTO.setFinalDate(null);
        List<Sort.Order> orders = List.of(Sort.Order.asc("initialDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<UnityHistoricalView> pagedResult = new PageImpl<>(unitiesHistorical, paging, unitiesHistorical.size());

        doReturn(pagedResult).when(unityHistoricalRepository)
                .find(anyString(), anyString(), anyString(), anyString(), any(Pageable.class));
        doReturn(new byte[50]).when(reportService).unityHistoricalReport(any(), any());

        byte[] reportReturn = unityHistoricalService.unityHistoricalReport(filterDTO, List.of(), List.of(), TypeDocEnum.XLSX);

        assertNotNull(reportReturn);
    }

    @Test
    void shouldThrowExceptionReportEmpty() {
        UnityHistoricalViewFilterDTO filterDTO = new EasyRandom().nextObject(UnityHistoricalViewFilterDTO.class);
        filterDTO.setInitialDate(null);
        filterDTO.setFinalDate(null);
        List<Sort.Order> orders = List.of(Sort.Order.asc("initialDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<UnityHistoricalView> pagedResult = new PageImpl<>(List.of(), paging, 0);

        doReturn(pagedResult).when(unityHistoricalRepository)
                .find(anyString(), anyString(), anyString(), anyString(), any(Pageable.class));

        Exception e = assertThrows(IllegalArgumentException.class,
                ()-> unityHistoricalService.unityHistoricalReport(filterDTO, List.of(), List.of(), TypeDocEnum.TXT));

        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void shouldThrowExceptionReportError() throws JRException, IOException{
        List<UnityHistoricalView> unitiesHistorical =
                new EasyRandom().objects(UnityHistoricalView.class, 5).collect(Collectors.toList());
        UnityHistoricalViewFilterDTO filterDTO = new EasyRandom().nextObject(UnityHistoricalViewFilterDTO.class);
        filterDTO.setInitialDate(null);
        filterDTO.setFinalDate(null);
        List<Sort.Order> orders = List.of(Sort.Order.asc("initialDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<UnityHistoricalView> pagedResult = new PageImpl<>(unitiesHistorical, paging, unitiesHistorical.size());

        doReturn(pagedResult).when(unityHistoricalRepository)
                .find(anyString(), anyString(), anyString(), anyString(), any(Pageable.class));
        doThrow(JRException.class).when(reportService).unityHistoricalReport(any(), any());

        Exception e = assertThrows(IllegalArgumentException.class,
                ()-> unityHistoricalService.unityHistoricalReport(filterDTO, List.of(), List.of(), TypeDocEnum.TXT));

        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }
}