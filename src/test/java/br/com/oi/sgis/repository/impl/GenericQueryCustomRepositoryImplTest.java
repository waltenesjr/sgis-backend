package br.com.oi.sgis.repository.impl;

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
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GenericQueryCustomRepositoryImplTest {

    @InjectMocks
    private GenericQueryCustomRepositoryImpl genericQueryCustomRepository;

    @Mock
    private EntityManager entityManager;
    @Test
    void executeQuery() {
        List<Object> returnedItens = new EasyRandom().objects(Object.class, 20).collect(Collectors.toList());
        Query query = Mockito.mock(Query.class);
        Mockito.doReturn(query).when(entityManager).createNativeQuery(Mockito.anyString());
        Mockito.doReturn(returnedItens).when(query).getResultList();

        List<Object> result = genericQueryCustomRepository.executeQuery("Teste");

        assertNotNull(result);
        assertEquals(returnedItens.size(), result.size());
        assertEquals(returnedItens.get(0), result.get(0));
    }

    @Test
    void executeQueryError() {
        Mockito.doThrow(QuerySyntaxException.class).when(entityManager).createNativeQuery(Mockito.anyString());
        Exception e = assertThrows(IllegalArgumentException.class, ()->genericQueryCustomRepository.executeQuery("Teste"));
        assertEquals(MessageUtils.ERROR_QUERY_REPORT.getDescription(), e.getMessage());
    }
}