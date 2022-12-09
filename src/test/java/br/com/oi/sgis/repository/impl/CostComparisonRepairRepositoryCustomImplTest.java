package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.CostComparisonRepDTO;
import br.com.oi.sgis.dto.EquipamentTypeRepairDTO;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CostComparisonRepairRepositoryCustomImplTest {
    @InjectMocks
    private CostComparisonRepairRepositoryCustomImpl costComparisonRepairRepositoryCustom;

    @Mock
    private EntityManager entityManager;

    @Test
    void findCostRepair() {
        List<CostComparisonRepDTO> returnedItens = new EasyRandom().objects(CostComparisonRepDTO.class, 20).collect(Collectors.toList());
        Query query = Mockito.mock(Query.class);
        Mockito.doReturn(query).when(entityManager).createNativeQuery(Mockito.anyString(), Mockito.eq(CostComparisonRepDTO.class));
        Mockito.doReturn(returnedItens).when(query).getResultList();

        List<CostComparisonRepDTO> result = costComparisonRepairRepositoryCustom.findCostRepair("Teste", LocalDateTime.now(), LocalDateTime.now());

        assertNotNull(result);
        assertEquals(returnedItens.size(), result.size());
        assertEquals(returnedItens.get(0), result.get(0));
    }

    @Test
    void findCostRepairError() {
        Mockito.doThrow(QuerySyntaxException.class).when(entityManager).createNativeQuery(Mockito.anyString(), Mockito.eq(CostComparisonRepDTO.class));
        Exception e = assertThrows(IllegalArgumentException.class, ()->costComparisonRepairRepositoryCustom.findCostRepair("Teste", null, null));
        assertEquals(MessageUtils.ERROR_QUERY_REPORT.getDescription(), e.getMessage());
    }
}