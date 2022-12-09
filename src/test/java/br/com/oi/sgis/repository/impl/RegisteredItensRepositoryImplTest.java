package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.RegisteredItensCriteriaDTO;
import br.com.oi.sgis.dto.RegisteredItensDTO;
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
class RegisteredItensRepositoryImplTest {
    @Mock
    private EntityManager entityManager;
    @InjectMocks
    private RegisteredItensRepositoryImpl repository;

    @Test
    void findRegisteredItens() {
        RegisteredItensCriteriaDTO criteriaDTO = new EasyRandom().nextObject(RegisteredItensCriteriaDTO.class);
        List<RegisteredItensDTO> returnedItens = new EasyRandom().objects(RegisteredItensDTO.class, 20).collect(Collectors.toList());
        TypedQuery<RegisteredItensDTO> query = Mockito.mock(TypedQuery.class);
        Mockito.doReturn(query).when(entityManager).createQuery(Mockito.anyString(), Mockito.eq(RegisteredItensDTO.class));
        Mockito.doReturn(returnedItens).when(query).getResultList();

        List<RegisteredItensDTO> result = repository.findRegisteredItens(criteriaDTO);

        assertNotNull(result);
        assertEquals(returnedItens.size(), result.size());
        assertEquals(returnedItens.get(0), result.get(0));
    }

    @Test
    void findRegisteredItensWithException() {
        RegisteredItensCriteriaDTO criteriaDTO = new EasyRandom().nextObject(RegisteredItensCriteriaDTO.class);
        Mockito.doThrow(QuerySyntaxException.class).when(entityManager).createQuery(Mockito.anyString(), Mockito.eq(RegisteredItensDTO.class));
        Exception e = assertThrows(IllegalArgumentException.class, ()->repository.findRegisteredItens(criteriaDTO));
        assertEquals(MessageUtils.ERROR_QUERY_REPORT.getDescription(), e.getMessage());
    }

}