package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.StockSummaryCriteriaDTO;
import br.com.oi.sgis.dto.StockSummaryDTO;
import br.com.oi.sgis.enums.FilteringEnum;
import br.com.oi.sgis.util.MessageUtils;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StockSummaryRepositoryImplTest {

    @InjectMocks
    private StockSummaryRepositoryImpl stockSummaryRepository;
    @Mock
    private EntityManager entityManager;

    @Test
    void findBySummaryParams() {
        StockSummaryCriteriaDTO criteriaDTO = new EasyRandom().nextObject(StockSummaryCriteriaDTO.class);
        List<StockSummaryDTO> returnedItens = getStockSummaryDTOS(criteriaDTO, FilteringEnum.TUDO);

        List<StockSummaryDTO> result = stockSummaryRepository.findBySummaryParams(criteriaDTO);

        assertNotNull(result);
        assertEquals(returnedItens.size(), result.size());
        assertEquals(returnedItens.get(0), result.get(0));
    }

    @Test
    void findBySummaryParamsWithException() {
        StockSummaryCriteriaDTO criteriaDTO = new EasyRandom().nextObject(StockSummaryCriteriaDTO.class);
        Mockito.doThrow(QuerySyntaxException.class).when(entityManager).createNativeQuery(Mockito.anyString(), Mockito.eq(StockSummaryDTO.class));

        Exception e = assertThrows(IllegalArgumentException.class, ()->stockSummaryRepository.findBySummaryParams(criteriaDTO));
        assertEquals(MessageUtils.ERROR_QUERY_REPORT.getDescription(), e.getMessage());
    }


    @Test
    void findBySummaryParamsFilterMAX() {
        StockSummaryCriteriaDTO criteriaDTO = new EasyRandom().nextObject(StockSummaryCriteriaDTO.class);
        List<StockSummaryDTO> returnedItens = getStockSummaryDTOS(criteriaDTO, FilteringEnum.MAXIMO);

        List<StockSummaryDTO> result = stockSummaryRepository.findBySummaryParams(criteriaDTO);

        assertNotNull(result);
        assertEquals(returnedItens.size(), result.size());
        assertEquals(returnedItens.get(0), result.get(0));
    }

    @Test
    void findBySummaryParamsFilterREPOS() {
        StockSummaryCriteriaDTO criteriaDTO = new EasyRandom().nextObject(StockSummaryCriteriaDTO.class);
        List<StockSummaryDTO> returnedItens = getStockSummaryDTOS(criteriaDTO, FilteringEnum.MAXIMO);

        List<StockSummaryDTO> result = stockSummaryRepository.findBySummaryParams(criteriaDTO);

        assertNotNull(result);
        assertEquals(returnedItens.size(), result.size());
        assertEquals(returnedItens.get(0), result.get(0));
    }

    private List<StockSummaryDTO> getStockSummaryDTOS(StockSummaryCriteriaDTO criteriaDTO, FilteringEnum maximo) {
        criteriaDTO.setFiltering(maximo);
        List<StockSummaryDTO> returnedItens = new EasyRandom().objects(StockSummaryDTO.class, 20).collect(Collectors.toList());
        Query query = Mockito.mock(Query.class);
        Mockito.doReturn(query).when(entityManager).createNativeQuery(Mockito.anyString(), Mockito.eq(StockSummaryDTO.class));
        Mockito.doReturn(returnedItens).when(query).getResultList();
        return returnedItens;
    }

    @Test
    void findBySummaryParamsFilterMIN() {
        StockSummaryCriteriaDTO criteriaDTO = new EasyRandom().nextObject(StockSummaryCriteriaDTO.class);
        List<StockSummaryDTO> returnedItens = getStockSummaryDTOS(criteriaDTO, FilteringEnum.MINIMO);

        List<StockSummaryDTO> result = stockSummaryRepository.findBySummaryParams(criteriaDTO);

        assertNotNull(result);
        assertEquals(returnedItens.size(), result.size());
        assertEquals(returnedItens.get(0), result.get(0));
    }
}