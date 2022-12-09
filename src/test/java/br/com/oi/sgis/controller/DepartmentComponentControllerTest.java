package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DepartmentComponentDTO;
import br.com.oi.sgis.dto.PaginateDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.SortDTO;
import br.com.oi.sgis.service.DepartmentComponentService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DepartmentComponentControllerTest {

    @InjectMocks
    DepartmentComponentController departmentComponentController;

    @Mock
    DepartmentComponentService departmentComponentService;

    @Test
    void listByDeparmentUserPaginated(){
        List<DepartmentComponentDTO> departmentComponents = new EasyRandom()
                .objects(DepartmentComponentDTO.class, 2).collect(Collectors.toList());
        SortDTO sort = SortDTO.builder().property("department").order("asc").build();
        PaginateDTO paginate = PaginateDTO.builder().totalItens(2).numberOfItens(2).totalPages(1).activePage(1).sorts(List.of(sort)).build();
        PaginateResponseDTO<DepartmentComponentDTO> expectedResponse =
                PaginateResponseDTO.<DepartmentComponentDTO>builder().data(departmentComponents).paginate(paginate).build();

        Mockito.doReturn(expectedResponse).when(departmentComponentService)
                .listByDeparmentUserPaginated(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(), Mockito.anyList());

        ResponseEntity<PaginateResponseDTO<DepartmentComponentDTO>> response =
                departmentComponentController.listByDeparmentUserPaginated(1, 10, List.of("department"), List.of());

        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        assertEquals(expectedResponse.getData(), response.getBody().getData());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void listByDeparmentUserPaginatedEmpty(){
        SortDTO sort = SortDTO.builder().property("department").order("asc").build();
        PaginateDTO paginate = PaginateDTO.builder().totalItens(2).numberOfItens(2).totalPages(1).activePage(1).sorts(List.of(sort)).build();
        PaginateResponseDTO<DepartmentComponentDTO> expectedResponse =
                PaginateResponseDTO.<DepartmentComponentDTO>builder().data(List.of()).paginate(paginate).build();

        Mockito.doReturn(expectedResponse).when(departmentComponentService)
                .listByDeparmentUserPaginated(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(), Mockito.anyList());

        ResponseEntity<PaginateResponseDTO<DepartmentComponentDTO>> response =
                departmentComponentController.listByDeparmentUserPaginated(1, 10, List.of("department"), List.of());

        assert(response.getBody().getData().isEmpty());
        assertEquals(200, response.getStatusCode().value());
    }

}