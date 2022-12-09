package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.ProductivityComparisonDTO;
import br.com.oi.sgis.repository.impl.ProductivityComparisonRepositoryCustomImpl;
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
class ProductivityComparisonRepositoryCustomImplTest {

    @InjectMocks
    private ProductivityComparisonRepositoryCustomImpl productivityComparisonRepository;

    @Mock
    private EntityManager entityManager;

    @Test
    void findProductivityComparisonByTechnical() {
        List<ProductivityComparisonDTO> returnedItems = new EasyRandom().objects(ProductivityComparisonDTO.class, 20).collect(Collectors.toList());
        Query query = Mockito.mock(Query.class);
        Mockito.doReturn(query).when(entityManager).createNativeQuery(Mockito.anyString(), Mockito.eq(ProductivityComparisonDTO.class));
        Mockito.doReturn(returnedItems).when(query).getResultList();

        List<ProductivityComparisonDTO> result =
                productivityComparisonRepository.findProductivityComparisonByTechnical("RJ-OI-ARC", null, null, null);

        assertNotNull(result);
        assertEquals(returnedItems.size(), result.size());
        assertEquals(returnedItems.get(0), result.get(0));
    }

    @Test
    void findProductivityComparisonByTechnicalWithAllParameters() {
        List<ProductivityComparisonDTO> returnedItems = new EasyRandom().objects(ProductivityComparisonDTO.class, 20).collect(Collectors.toList());
        Query query = Mockito.mock(Query.class);
        Mockito.doReturn(query).when(entityManager).createNativeQuery(Mockito.anyString(), Mockito.eq(ProductivityComparisonDTO.class));
        Mockito.doReturn(returnedItems).when(query).getResultList();

        List<ProductivityComparisonDTO> result =
                productivityComparisonRepository.findProductivityComparisonByTechnical("RJ-OI-ARC", "Technical", LocalDateTime.now(), LocalDateTime.now().plusDays(2));

        assertNotNull(result);
        assertEquals(returnedItems.size(), result.size());
        assertEquals(returnedItems.get(0), result.get(0));
    }

    @Test
    void findByProductivityComparisonByTechnicalError() {
        Query query = Mockito.mock(Query.class);
        Mockito.doThrow(QuerySyntaxException.class).when(entityManager).createNativeQuery(Mockito.anyString(), Mockito.eq(ProductivityComparisonDTO.class));
        Exception e = assertThrows(IllegalArgumentException.class,
                ()->productivityComparisonRepository.findProductivityComparisonByTechnical(null, null, null, null));
        assertEquals(MessageUtils.ERROR_QUERY_REPORT.getDescription(), e.getMessage());
    }
}