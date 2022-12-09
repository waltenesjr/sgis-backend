package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.MovItensReportDTO;
import br.com.oi.sgis.entity.view.MovItensView;
import br.com.oi.sgis.enums.MovItensReportOrderEnum;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.repository.MovItensRepository;
import br.com.oi.sgis.service.validator.Validator;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MovItensServiceTest {

    @InjectMocks
    private MovItensService movItensService;
    @Mock
    private Validator validator;
    @Mock
    private MovItensRepository movItensRepository;
    @Mock
    private ReportService reportService;
    private  List<MovItensView> itensCrieria;
    private MovItensReportDTO movItensReportDTO;

    @BeforeEach
    void setUp(){
        itensCrieria = new EasyRandom().objects(MovItensView.class, 20).collect(Collectors.toList());
        movItensReportDTO = new EasyRandom().nextObject(MovItensReportDTO.class);

    }

    @Test
    void shouldListItens() throws JRException, IOException {
        movItensReportDTO.setOrderBy(MovItensReportOrderEnum.DATA);
        movItensReportDTO.setBreakTotals(false);
        Mockito.doReturn(itensCrieria).when(movItensRepository).findByParameters(Mockito.any());
        Mockito.doReturn(new byte[0]).when(reportService).fillMovItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        byte[] itens = movItensService.report(movItensReportDTO, TypeDocEnum.XLSX);
        assertNotNull(itens);
    }

    @Test
    void shouldListItensGroupByDate() throws JRException, IOException {
        listBySelectedGroup(MovItensReportOrderEnum.DATA);
    }

    private void listBySelectedGroup(MovItensReportOrderEnum orderEnum) throws JRException, IOException {
        movItensReportDTO.setOrderBy(orderEnum);
        Mockito.doReturn(itensCrieria).when(movItensRepository).findByParameters(Mockito.any());
        Mockito.doReturn(new byte[0]).when(reportService).fillMovItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        byte[] itens = movItensService.report(movItensReportDTO, TypeDocEnum.XLSX);
        assertNotNull(itens);
    }

    @Test
    void shouldWarningItensEmpty() {
        List<MovItensView> itensCrieria = new ArrayList<>();
        MovItensReportDTO movItensReportDTO = new EasyRandom().nextObject(MovItensReportDTO.class);
        Mockito.doReturn(itensCrieria).when(movItensRepository).findByParameters(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()-> movItensService.report(movItensReportDTO, TypeDocEnum.XLSX));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void shouldListItensGroupByUnityCode() throws JRException, IOException {
        listBySelectedGroup(MovItensReportOrderEnum.PARA_COD_PLACA);
    }

    @Test
    void shouldListItensGroupByResponsibleFrom() throws JRException, IOException {
        listBySelectedGroup(MovItensReportOrderEnum.DE_SIGLA_PROP);
    }

    @Test
    void shouldListItensGroupByResponsibleTo() throws JRException, IOException {
        listBySelectedGroup(MovItensReportOrderEnum.PARA_SIGLA_PROP);
    }

    @Test
    void shouldListItensGroupBySituationFrom() throws JRException, IOException {
        listBySelectedGroup(MovItensReportOrderEnum.DE_COD_SIT);
    }

    @Test
    void shouldListItensGroupBySituationTo() throws JRException, IOException {
        listBySelectedGroup(MovItensReportOrderEnum.PARA_COD_SIT);
    }

    @Test
    void shouldListItensgroupByTechnicianFrom() throws JRException, IOException {
        listBySelectedGroup(MovItensReportOrderEnum.DE_TECNICO);
    }

    @Test
    void shouldListItensGroupByTechnicianTo() throws JRException, IOException {
        listBySelectedGroup(MovItensReportOrderEnum.PARA_TECNICO);
    }




}