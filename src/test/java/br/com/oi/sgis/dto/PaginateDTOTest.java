package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PaginateDTOTest {

    @Test
    void paginateTestDto(){
        PaginateDTO paginateDTO = PaginateDTO.builder().activePage(1).numberOfItens(5)
                .sorts(List.of(SortDTO.builder().order("ASC").property("id").build()))
                .build();

        paginateDTO.setTotalPages(1);
        paginateDTO.setTotalItens(5);

        Assertions.assertEquals(1, paginateDTO.getTotalPages());
        Assertions.assertEquals(5, paginateDTO.getTotalItens());
        Assertions.assertEquals(5, paginateDTO.getNumberOfItens());
        Assertions.assertEquals(1, paginateDTO.getActivePage());
        Assertions.assertEquals(1, paginateDTO.getSorts().size());
    }
}
