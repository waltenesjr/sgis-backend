package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.EquipamentTypeRepairDTO;
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EquipmentTypeRepairRepositoryCustomImplTest {
    @InjectMocks
    private EquipmentTypeRepairRepositoryCustomImpl equipmentTypeRepairRepositoryCustom;

    @Mock
    private EntityManager entityManager;

    @Test
    void findByRepairCenter() {
        List<EquipamentTypeRepairDTO> returnedItens = new EasyRandom().objects(EquipamentTypeRepairDTO.class, 20).collect(Collectors.toList());
        Query query = Mockito.mock(Query.class);
        Mockito.doReturn(query).when(entityManager).createNativeQuery(Mockito.anyString(), Mockito.eq(EquipamentTypeRepairDTO.class));
        Mockito.doReturn(returnedItens).when(query).getResultList();

        List<EquipamentTypeRepairDTO> result = equipmentTypeRepairRepositoryCustom.findByRepairCenter("Teste", LocalDateTime.now(), LocalDateTime.now());

        assertNotNull(result);
        assertEquals(returnedItens.size(), result.size());
        assertEquals(returnedItens.get(0), result.get(0));
    }

    @Test
    void findByRepairCenterError() {
        Query query = Mockito.mock(Query.class);
        Mockito.doThrow(QuerySyntaxException.class).when(entityManager).createNativeQuery(Mockito.anyString(), Mockito.eq(EquipamentTypeRepairDTO.class));
        Exception e = assertThrows(IllegalArgumentException.class, ()->equipmentTypeRepairRepositoryCustom.findByRepairCenter("Teste", null, null));
        assertEquals(MessageUtils.ERROR_QUERY_REPORT.getDescription(), e.getMessage());
    }
}