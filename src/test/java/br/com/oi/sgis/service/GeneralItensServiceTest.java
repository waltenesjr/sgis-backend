package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GeneralItensCriteriaDTO;
import br.com.oi.sgis.dto.GeneralItensDTO;
import br.com.oi.sgis.enums.GeneralItensReportBreakEnum;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GeneralItensServiceTest {

    @Mock
    private UnityRepository generalItensRepository;
    @Mock
    private ReportService reportService;

    @InjectMocks
    private GeneralItensService generalItensService;

    GeneralItensCriteriaDTO criteriaDTO;
    List<GeneralItensDTO> returnedItens;

    @BeforeEach
    void setUp(){
        criteriaDTO = new EasyRandom().nextObject(GeneralItensCriteriaDTO.class);
        criteriaDTO.setSituationInitialDate(LocalDateTime.now());
        criteriaDTO.setSituationFinalDate(LocalDateTime.now().plusDays(5));
        criteriaDTO.setOrder(GeneralItensReportBreakEnum.ESTACAO);
        returnedItens = new EasyRandom().objects(GeneralItensDTO.class, 20).collect(Collectors.toList());
    }

    @Test
    void listItens() throws JRException, IOException {
        Mockito.doReturn(returnedItens).when(generalItensRepository).listGeneralItens(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillGeneralItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        byte[] report = generalItensService.report(criteriaDTO, TypeDocEnum.TXT);
        assertNotNull(report);
    }

    @Test
    void listItensGroupByUnity() throws JRException, IOException {
        Mockito.doReturn(returnedItens).when(generalItensRepository).listGeneralItens(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillGeneralItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        criteriaDTO.setOrder(GeneralItensReportBreakEnum.UNIDADE);
        byte[] report = generalItensService.report(criteriaDTO, TypeDocEnum.TXT);
        assertNotNull(report);
    }

    @Test
    void listItensGroupByEquipament() throws JRException, IOException {
        Mockito.doReturn(returnedItens).when(generalItensRepository).listGeneralItens(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillGeneralItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        criteriaDTO.setOrder(GeneralItensReportBreakEnum.EQUIPAMENTO);
        byte[] report = generalItensService.report(criteriaDTO, TypeDocEnum.TXT);
        assertNotNull(report);
    }
    @Test
    void listItensGroupByTechnician() throws JRException, IOException {
        Mockito.doReturn(returnedItens).when(generalItensRepository).listGeneralItens(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillGeneralItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        criteriaDTO.setOrder(GeneralItensReportBreakEnum.TECNICO);
        byte[] report = generalItensService.report(criteriaDTO, TypeDocEnum.TXT);
        assertNotNull(report);
    }
    @Test
    void listItensGroupBySituation() throws JRException, IOException {
        Mockito.doReturn(returnedItens).when(generalItensRepository).listGeneralItens(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillGeneralItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        criteriaDTO.setOrder(GeneralItensReportBreakEnum.SITUACAO);
        byte[] report = generalItensService.report(criteriaDTO, TypeDocEnum.TXT);
        assertNotNull(report);
    }
    @Test
    void listItensGroupByManufacturer() throws JRException, IOException {
        Mockito.doReturn(returnedItens).when(generalItensRepository).listGeneralItens(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillGeneralItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        criteriaDTO.setOrder(GeneralItensReportBreakEnum.FABRICANTE);
        byte[] report = generalItensService.report(criteriaDTO, TypeDocEnum.TXT);
        assertNotNull(report);
    }
    @Test
    void listItensGroupByResponsible() throws JRException, IOException {
        Mockito.doReturn(returnedItens).when(generalItensRepository).listGeneralItens(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillGeneralItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        criteriaDTO.setOrder(GeneralItensReportBreakEnum.RESPONSAVEL);
        byte[] report = generalItensService.report(criteriaDTO, TypeDocEnum.TXT);
        assertNotNull(report);
    }
    @Test
    void listItensGroupByDeposit() throws JRException, IOException {
        Mockito.doReturn(returnedItens).when(generalItensRepository).listGeneralItens(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillGeneralItensReport(Mockito.any(), Mockito.any(), Mockito.any(),  Mockito.any());

        criteriaDTO.setOrder(GeneralItensReportBreakEnum.DEPOSITORIO);
        byte[] report = generalItensService.report(criteriaDTO, TypeDocEnum.TXT);
        assertNotNull(report);
    }

    @Test
    void listItensGroupByNone() throws JRException, IOException {
        Mockito.doReturn(returnedItens).when(generalItensRepository).listGeneralItens(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillGeneralItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        criteriaDTO.setOrder(null);
        byte[] report = generalItensService.report(criteriaDTO, TypeDocEnum.TXT);
        assertNotNull(report);
    }

    @Test
    void listItensNull() {
        criteriaDTO.setOrder(null);
        Mockito.doReturn(List.of()).when(generalItensRepository).listGeneralItens(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->generalItensService.report(criteriaDTO, TypeDocEnum.TXT));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(),e.getMessage());
    }
}