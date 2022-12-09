package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class SortDTOTest {

    @Test
    void sortDTO(){
        SortDTO sortDTO = SortDTO.builder().order("ASC").property("id").build();

        Assertions.assertEquals("ASC", sortDTO.getOrder());
        Assertions.assertEquals("id", sortDTO.getProperty());
    }
    @Test
    void sortDTOWithOrderConstructor(){
        Sort.Order order = Sort.Order.asc("id");
        SortDTO sortDTO = new SortDTO(order);

        Assertions.assertEquals("ASC", sortDTO.getOrder());
        Assertions.assertEquals("id", sortDTO.getProperty());
    }

    @Test
    void sortDTOWithEmptyConstructor(){
        SortDTO sortDTO = new SortDTO();
        sortDTO.setProperty("id");
        sortDTO.setOrder("ASC");

        Assertions.assertEquals("ASC", sortDTO.getOrder());
        Assertions.assertEquals("id", sortDTO.getProperty());

    }

}
