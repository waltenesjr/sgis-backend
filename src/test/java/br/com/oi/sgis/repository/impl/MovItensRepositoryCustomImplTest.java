package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.MovItensReportDTO;
import br.com.oi.sgis.entity.view.MovItensView;
import br.com.oi.sgis.enums.MovItensReportOrderEnum;
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
class MovItensRepositoryCustomImplTest {

    @InjectMocks
    private MovItensRepositoryCustomImpl movItensRepositoryCustom;
    @Mock
    private EntityManager entityManager;


    @Test
    void shouldFindByParams(){
        MovItensReportDTO movItensReportDTO = new EasyRandom().nextObject(MovItensReportDTO.class);
        List<MovItensView> itensCrieria = new EasyRandom().objects(MovItensView.class, 20).collect(Collectors.toList());
        movItensReportDTO.setOrderBy(MovItensReportOrderEnum.PARA_COD_PLACA);
        Query query = Mockito.mock(Query.class);
        Mockito.doReturn(query).when(entityManager).createNativeQuery(Mockito.anyString(), Mockito.eq(MovItensView.class));
        Mockito.doReturn(itensCrieria).when(query).getResultList();

        List<MovItensView> result = movItensRepositoryCustom.findByParameters(movItensReportDTO);

        assertNotNull(result);
        assertEquals(itensCrieria.size(), result.size());
        assertEquals(itensCrieria.get(0), result.get(0));
    }

    @Test
    void shouldFindByParamsWithException(){
        MovItensReportDTO movItensReportDTO = new EasyRandom().nextObject(MovItensReportDTO.class);
        movItensReportDTO.setOrderBy(MovItensReportOrderEnum.PARA_COD_PLACA);
        Mockito.doThrow(QuerySyntaxException.class).when(entityManager).createNativeQuery(Mockito.anyString(), Mockito.eq(MovItensView.class));

        Exception e = assertThrows(IllegalArgumentException.class, ()->movItensRepositoryCustom.findByParameters(movItensReportDTO));
        assertEquals(MessageUtils.ERROR_QUERY_REPORT.getDescription(), e.getMessage());
    }

}