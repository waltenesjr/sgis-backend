package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.AreaEquipamentDTO;
import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.StockSummaryCriteriaDTO;
import br.com.oi.sgis.dto.StockSummaryDTO;
import br.com.oi.sgis.entity.view.GyreView;
import br.com.oi.sgis.enums.FilteringEnum;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.repository.DepartmentUnityRepository;
import br.com.oi.sgis.repository.GyreViewRepository;
import br.com.oi.sgis.util.MessageUtils;
import lombok.SneakyThrows;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class StockSummaryServiceTest {

    @InjectMocks
    private StockSummaryService stockSummaryService;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private AreaEquipamentService areaEquipamentService;
    @Mock
    private DepartmentUnityRepository departmentUnityRepository;
    @Mock
    private GyreViewRepository gyreViewRepository;
    @Mock
    private ReportService reportService;

    private StockSummaryCriteriaDTO criteriaDTO;

    @BeforeEach
    private void setUp(){
        LocalDateTime finalP = LocalDateTime.now();
        LocalDateTime initial = finalP.minusDays(5);
        criteriaDTO = StockSummaryCriteriaDTO.builder().analysis(true).filtering(FilteringEnum.TUDO)
                .finalPeriod(finalP).initialPeriod(initial).modelCode("code").responsibleCode("responsible").build();
    }

    @SneakyThrows
    @Test
    void report() {
        DepartmentDTO departmentDTO = DepartmentDTO.builder().build();
        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().build();
        List<GyreView> entraceAndExits = new EasyRandom().objects(GyreView.class, 5).collect(Collectors.toList());
        List<StockSummaryDTO> stockSummaryDTO = new EasyRandom().objects(StockSummaryDTO.class, 5).collect(Collectors.toList());

        Mockito.doReturn(departmentDTO).when(departmentService).findById(any());
        Mockito.doReturn(areaEquipamentDTO).when(areaEquipamentService).findById(any());
        Mockito.doReturn(entraceAndExits.size()).when(gyreViewRepository).findEntranceByUnityCode(any(), any(), any());
        Mockito.doReturn(entraceAndExits.size()).when(gyreViewRepository).findExitByUnityCode(any(), any(), any());
        Mockito.doReturn(stockSummaryDTO).when(departmentUnityRepository).findBySummaryParams(any());
        Mockito.doReturn(new byte[50]).when(reportService).fillStockSummaryReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        byte[] report = stockSummaryService.report(criteriaDTO, TypeDocEnum.XLSX);

        assertNotNull(report);
    }

    @SneakyThrows
    @Test
    void listStockNotAnalysis() {
        DepartmentDTO departmentDTO = DepartmentDTO.builder().build();
        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().build();
        List<StockSummaryDTO> stockSummaryDTO = new EasyRandom().objects(StockSummaryDTO.class, 5).collect(Collectors.toList());

        criteriaDTO.setAnalysis(false);
        Mockito.doReturn(departmentDTO).when(departmentService).findById(any());
        Mockito.doReturn(areaEquipamentDTO).when(areaEquipamentService).findById(any());
        Mockito.doReturn(stockSummaryDTO).when(departmentUnityRepository).findBySummaryParams(any());
        Mockito.doReturn(new byte[50]).when(reportService).fillStockSummaryReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        byte[] report = stockSummaryService.report(criteriaDTO, TypeDocEnum.XLSX);

        assertNotNull(report);

    }

    @SneakyThrows
    @Test
    void listStockInvalidPeriod() {
        DepartmentDTO departmentDTO = DepartmentDTO.builder().build();
        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().build();

        criteriaDTO.setInitialPeriod(null);
        Mockito.doReturn(departmentDTO).when(departmentService).findById(any());
        Mockito.doReturn(areaEquipamentDTO).when(areaEquipamentService).findById(any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> stockSummaryService.report(criteriaDTO, TypeDocEnum.XLSX));
        assertEquals(MessageUtils.PERIOD_NULL.getDescription(), e.getMessage());

    }

    @SneakyThrows
    @Test
    void listStockEmpty() {
        DepartmentDTO departmentDTO = DepartmentDTO.builder().build();
        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().build();

        criteriaDTO.setAnalysis(false);
        Mockito.doReturn(departmentDTO).when(departmentService).findById(any());
        Mockito.doReturn(areaEquipamentDTO).when(areaEquipamentService).findById(any());
        Mockito.doReturn(List.of()).when(departmentUnityRepository).findBySummaryParams(any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> stockSummaryService.report(criteriaDTO, TypeDocEnum.XLSX));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @SneakyThrows
    @Test
    void listStockReportBuildError() {
        DepartmentDTO departmentDTO = DepartmentDTO.builder().build();
        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().build();
        List<GyreView> entraceAndExits = new EasyRandom().objects(GyreView.class, 5).collect(Collectors.toList());
        List<StockSummaryDTO> stockSummaryDTO = new EasyRandom().objects(StockSummaryDTO.class, 5).collect(Collectors.toList());

        Mockito.doReturn(departmentDTO).when(departmentService).findById(any());
        Mockito.doReturn(areaEquipamentDTO).when(areaEquipamentService).findById(any());
        Mockito.doReturn(entraceAndExits.size()).when(gyreViewRepository).findEntranceByUnityCode(any(), any(), any());
        Mockito.doThrow(SqlScriptParserException.class).when(gyreViewRepository).findExitByUnityCode(any(), any(), any());
        Mockito.doReturn(stockSummaryDTO).when(departmentUnityRepository).findBySummaryParams(any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> stockSummaryService.report(criteriaDTO, TypeDocEnum.XLSX));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }
}