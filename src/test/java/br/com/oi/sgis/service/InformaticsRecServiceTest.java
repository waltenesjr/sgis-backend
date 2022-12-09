package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.SapOperationHistoryDTO;
import br.com.oi.sgis.dto.SapOperationHistoryReportDTO;
import br.com.oi.sgis.repository.InformaticsRecRepository;
import br.com.oi.sgis.util.MessageUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InformaticsRecServiceTest {

    @InjectMocks
    private InformaticsRecService informaticsRecService;

    @Mock
    private InformaticsRecRepository informaticsRecRepository;

    @Test
    void shouldGetSapOperationHistory(){
        List<SapOperationHistoryReportDTO> sapOperationHistoryReportDTOs = new EasyRandom().objects(SapOperationHistoryReportDTO.class, 5).collect(Collectors.toList());
        SapOperationHistoryDTO dto = new EasyRandom().nextObject(SapOperationHistoryDTO.class);
        dto.setInitialDate(LocalDateTime.of(2000, 1, 01, 00, 00));
        dto.setFinalDate(LocalDateTime.now());
        List<Sort.Order> orders = List.of(Sort.Order.asc("unity"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<SapOperationHistoryReportDTO> pagedResult = new PageImpl<>(sapOperationHistoryReportDTOs, paging, sapOperationHistoryReportDTOs.size());

        Mockito.doReturn(pagedResult).when(informaticsRecRepository).findSapOperationHistory(dto, paging);
        PaginateResponseDTO<SapOperationHistoryReportDTO> sapOperationHistoryReportDTOReturn =
                informaticsRecService.getSapOperationHistory(dto, 0, 10, List.of("unity"), List.of());

        assertEquals(sapOperationHistoryReportDTOs.size(), sapOperationHistoryReportDTOReturn.getData().size());
    }

    @Test
    void shouldReturnInvalidPeriodMessage(){
        SapOperationHistoryDTO dto = new EasyRandom().nextObject(SapOperationHistoryDTO.class);
        dto.setInitialDate(LocalDateTime.now());
        dto.setFinalDate(LocalDateTime.of(2000, 1, 01, 00, 00));

        Throwable exception = catchThrowable(() -> informaticsRecService.getSapOperationHistory(dto, 0, 10, List.of("unity"), List.of()));

        assertEquals(MessageUtils.INVALID_PERIOD.getDescription(), exception.getMessage());
    }

    @Test
    void shouldGetSapOperationHistoryWithNullSearch(){
        List<SapOperationHistoryReportDTO> sapOperationHistoryReportDTOs = new EasyRandom().objects(SapOperationHistoryReportDTO.class, 5).collect(Collectors.toList());
        SapOperationHistoryDTO dto = new EasyRandom().nextObject(SapOperationHistoryDTO.class);
        dto.setSearch(null);
        dto.setInitialDate(LocalDateTime.of(2000, 1, 01, 00, 00));
        dto.setFinalDate(LocalDateTime.now());
        List<Sort.Order> orders = List.of(Sort.Order.asc("unity"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<SapOperationHistoryReportDTO> pagedResult = new PageImpl<>(sapOperationHistoryReportDTOs, paging, sapOperationHistoryReportDTOs.size());

        Mockito.doReturn(pagedResult).when(informaticsRecRepository).findSapOperationHistory(dto, paging);
        PaginateResponseDTO<SapOperationHistoryReportDTO> sapOperationHistoryReportDTOReturn =
                informaticsRecService.getSapOperationHistory(dto, 0, 10, List.of("unity"), List.of());

        assertEquals(sapOperationHistoryReportDTOs.size(), sapOperationHistoryReportDTOReturn.getData().size());
    }
}
