package br.com.oi.sgis.util;

import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Unity;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class PageableUtilTest {

    @Test
    void shoulCreatePaginateResponse(){
        List<Unity> unities = new EasyRandom().objects(Unity.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Object> pagedResult = new PageImpl(unities, paging, unities.size());

        PaginateResponseDTO<Object> response = PageableUtil.paginate(pagedResult);

        Assertions.assertEquals(unities.size(), response.getPaginate().getTotalItens());
    }

    @Test
    void shouldReturnCrrectPageNumber(){
        int pageNumber = PageableUtil.correctPageNo(1);
        Assertions.assertEquals(0, pageNumber);
    }
}
