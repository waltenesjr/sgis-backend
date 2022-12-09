package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PaginateDTOResponseDTOTest {

    @Test
    void paginateResponseDTOTest(){
        List<String> fruits = List.of("abacaxi", "uva", "maçã", "morango", "laranja");
        PaginateResponseDTO paginateResponseDTO = new PaginateResponseDTO();
        paginateResponseDTO.setPaginate(PaginateDTO.builder().activePage(1).numberOfItens(5).totalPages(1).build());
        paginateResponseDTO.setData(fruits);

        Assertions.assertEquals(fruits.size(), paginateResponseDTO.getData().size());
        Assertions.assertEquals(5, paginateResponseDTO.getPaginate().getNumberOfItens());
    }
}
