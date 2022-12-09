package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.GeneralItensCriteriaDTO;
import br.com.oi.sgis.dto.GeneralItensDTO;
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
class GeneralItensRepositoryImplTest {

    @InjectMocks
    private GeneralItensRepositoryImpl generalItensRepository;

    @Mock
    private EntityManager entityManager;

    @Test
    void listGeneralItens() {
        GeneralItensCriteriaDTO criteriaDTO = new EasyRandom().nextObject(GeneralItensCriteriaDTO.class);
        List<GeneralItensDTO> returnedItens = new EasyRandom().objects(GeneralItensDTO.class, 20).collect(Collectors.toList());
        TypedQuery<GeneralItensDTO> query = Mockito.mock(TypedQuery.class);
        Mockito.doReturn(query).when(entityManager).createQuery(Mockito.anyString(), Mockito.eq(GeneralItensDTO.class));
        Mockito.doReturn(returnedItens).when(query).getResultList();

        List<GeneralItensDTO> result = generalItensRepository.listGeneralItens(criteriaDTO);

        assertNotNull(result);
        assertEquals(returnedItens.size(), result.size());
        assertEquals(returnedItens.get(0), result.get(0));
    }

    @Test
    void shouldListGeneralWithException(){
        GeneralItensCriteriaDTO criteriaDTO = new EasyRandom().nextObject(GeneralItensCriteriaDTO.class);
        Mockito.doThrow(QuerySyntaxException.class).when(entityManager).createQuery(Mockito.anyString(), Mockito.eq(GeneralItensDTO.class));

        Exception e = assertThrows(IllegalArgumentException.class, ()->generalItensRepository.listGeneralItens(criteriaDTO));
        assertEquals(MessageUtils.ERROR_QUERY_REPORT.getDescription(), e.getMessage());
    }
}