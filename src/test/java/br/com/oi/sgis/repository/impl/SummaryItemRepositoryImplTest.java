package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.SummaryItemCriteriaReportDTO;
import br.com.oi.sgis.dto.SummaryItemViewDTO;
import br.com.oi.sgis.enums.ItensSumaryReportBreakEnum;
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
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SummaryItemRepositoryImplTest {

    @InjectMocks
    private SummaryItemRepositoryImpl summaryItemRepository;

    @Mock
    private EntityManager entityManager;

    @Test
    void shouldFindBySummaryParams(){
        SummaryItemCriteriaReportDTO criteria = new EasyRandom().nextObject(SummaryItemCriteriaReportDTO.class);
        List<SummaryItemViewDTO> itens =  new EasyRandom().objects(SummaryItemViewDTO.class, 15).collect(Collectors.toList());
        criteria.setBreakResults(ItensSumaryReportBreakEnum.ESTACAO);

        TypedQuery<SummaryItemViewDTO> query = Mockito.mock(TypedQuery.class);
        Mockito.doReturn(query).when(entityManager).createQuery(Mockito.anyString(), Mockito.eq(SummaryItemViewDTO.class));
        Mockito.doReturn(itens).when(query).getResultList();

        List<SummaryItemViewDTO> result = summaryItemRepository.findBySummaryParams(criteria);

        assertNotNull(result);
        assertEquals(itens.size(), result.size());
        assertEquals(itens.get(0), result.get(0));
    }


    @Test
    void shouldFindByParamsWithException() {
        SummaryItemCriteriaReportDTO criteria = new EasyRandom().nextObject(SummaryItemCriteriaReportDTO.class);
        Mockito.doThrow(QuerySyntaxException.class).when(entityManager).createQuery(Mockito.anyString(), Mockito.eq(SummaryItemViewDTO.class));

        Exception e = assertThrows(IllegalArgumentException.class, ()->summaryItemRepository.findBySummaryParams(criteria));
        assertEquals(MessageUtils.ERROR_QUERY_REPORT.getDescription(), e.getMessage());
    }
}