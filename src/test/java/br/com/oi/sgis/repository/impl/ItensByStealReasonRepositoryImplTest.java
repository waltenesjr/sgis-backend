package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.ItemBySituationViewDTO;
import br.com.oi.sgis.dto.ItensInstallByStealReasonCriteriaDTO;
import br.com.oi.sgis.dto.ItensInstallByStealReasonDTO;
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
class ItensByStealReasonRepositoryImplTest {

    @InjectMocks
    private ItensByStealReasonRepositoryImpl repository;
    @Mock
    private EntityManager entityManager;
    @Test
    void findByParamsSteal() {
        ItensInstallByStealReasonCriteriaDTO  criteriaDTO = new EasyRandom().nextObject(ItensInstallByStealReasonCriteriaDTO.class);
        List<ItensInstallByStealReasonDTO> returnedItens = new EasyRandom().objects(ItensInstallByStealReasonDTO.class, 20).collect(Collectors.toList());
        TypedQuery<ItensInstallByStealReasonDTO> query = Mockito.mock(TypedQuery.class);
        Mockito.doReturn(query).when(entityManager).createQuery(Mockito.anyString(), Mockito.eq(ItensInstallByStealReasonDTO.class));
        Mockito.doReturn(returnedItens).when(query).getResultList();

        List<ItensInstallByStealReasonDTO> result = repository.findByParamsSteal(criteriaDTO);

        assertNotNull(result);
        assertEquals(returnedItens.size(), result.size());
        assertEquals(returnedItens.get(0), result.get(0));
    }

    @Test
    void shouldFindByParamsWithException(){
        ItensInstallByStealReasonCriteriaDTO criteriaDTO = new EasyRandom().nextObject(ItensInstallByStealReasonCriteriaDTO.class);
        Mockito.doThrow(QuerySyntaxException.class).when(entityManager).createQuery(Mockito.anyString(), Mockito.eq(ItemBySituationViewDTO.class));

        Exception e = assertThrows(IllegalArgumentException.class, ()->repository.findByParamsSteal(criteriaDTO));
        assertEquals(MessageUtils.ERROR_QUERY_REPORT.getDescription(), e.getMessage());
    }
}