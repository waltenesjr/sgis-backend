package br.com.oi.sgis.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SortUtilTest {

    @Test
    void shouldCreateSorts(){
        List<String> sortAsc = List.of("id", "responsible");
        List<String> sortDesc = List.of("registerDate");

        Sort sort = SortUtil.createSorts(sortAsc, sortDesc);

        Assertions.assertEquals(3, (int) sort.get().count());
        Assertions.assertEquals(sortAsc.size(), (int) sort.get().filter(s -> s.getDirection().isAscending()).count());
        Assertions.assertEquals(sortDesc.size(), (int) sort.get().filter(s -> s.getDirection().isDescending()).count());
    }

}
