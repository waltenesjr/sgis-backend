package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ItemBySitReportCriteriaDTO;
import br.com.oi.sgis.dto.ItemBySituationViewDTO;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemBySituationServiceTest {
    @InjectMocks
    private ItemBySituationService itemBySituationService;

    @Mock
    private UnityRepository situationItemRepository;
    @Mock
    private ReportService reportService;

    @Mock
    private Validator<ItemBySitReportCriteriaDTO> validator;

    @Test
    void listItens() throws JRException, IOException {
        ItemBySitReportCriteriaDTO criteriaDTO = new EasyRandom().nextObject(ItemBySitReportCriteriaDTO.class);
        List<ItemBySituationViewDTO> itens = new EasyRandom().objects(ItemBySituationViewDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(itens).when(situationItemRepository).findSituationByParams(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillSitItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        byte[] itensReturned = itemBySituationService.report(criteriaDTO, TypeDocEnum.XLSX);
        assertNotNull(itensReturned);
    }

    @Test
    void listItensWithException() {
        ItemBySitReportCriteriaDTO criteriaDTO = ItemBySitReportCriteriaDTO.builder().build();
        Mockito.doReturn(List.of()).when(situationItemRepository).findSituationByParams(Mockito.any());
        Exception e =  assertThrows(IllegalArgumentException.class, ()-> itemBySituationService.report(criteriaDTO, TypeDocEnum.TXT));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }
}