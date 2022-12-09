package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.CostComparisonRepDTO;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ProcedureSitOutRepositoryImplTest {
    @InjectMocks
    private ProcedureSitOutRepositoryImpl procedureSitOutRepository;

    @Mock
    private EntityManager entityManager;
    @Test
    void interventionSituation() {
        StoredProcedureQuery query = Mockito.mock(StoredProcedureQuery.class);
        Mockito.doReturn(query).when(entityManager).createStoredProcedureQuery(Mockito.anyString());
        Mockito.doReturn("TESTE").when(query).getOutputParameterValue(Mockito.anyString());

        String result = procedureSitOutRepository.interventionSituation("Teste");

        assertNotNull(result);
        assertEquals("TESTE", result);


    }
}