package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.ItemBySitReportCriteriaDTO;
import br.com.oi.sgis.dto.ItemBySituationViewDTO;
import br.com.oi.sgis.dto.UserExtractionDTO;
import br.com.oi.sgis.dto.UserExtractionReportDTO;
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
class UserRegisterRepositoryCustomImplTest {

    @InjectMocks
    private UserRegisterRepositoryCustomImpl userRegisterRepositoryCustom;
    @Mock
    private EntityManager entityManager;

    @Test
    void findForExtraction() {
        UserExtractionDTO criteriaDTO = new EasyRandom().nextObject(UserExtractionDTO.class);
        List<UserExtractionReportDTO> returnedItens = new EasyRandom().objects(UserExtractionReportDTO.class, 20).collect(Collectors.toList());
        TypedQuery<UserExtractionReportDTO> query = Mockito.mock(TypedQuery.class);
        Mockito.doReturn(query).when(entityManager).createQuery(Mockito.anyString(), Mockito.eq(UserExtractionReportDTO.class));
        Mockito.doReturn(returnedItens).when(query).getResultList();

        List<UserExtractionReportDTO> result = userRegisterRepositoryCustom.findForExtraction(criteriaDTO);

        assertNotNull(result);
        assertEquals(returnedItens.size(), result.size());
        assertEquals(returnedItens.get(0), result.get(0));
    }

    @Test
    void findForExtractionWithException(){
        UserExtractionDTO criteriaDTO = new EasyRandom().nextObject(UserExtractionDTO.class);
        Mockito.doThrow(QuerySyntaxException.class).when(entityManager).createQuery(Mockito.anyString(), Mockito.eq(UserExtractionReportDTO.class));

        Exception e = assertThrows(IllegalArgumentException.class, ()->userRegisterRepositoryCustom.findForExtraction(criteriaDTO));
        assertEquals(MessageUtils.ERROR_QUERY_REPORT.getDescription(), e.getMessage());
    }
}