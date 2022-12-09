package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DepartmentComponentDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.DepartmentComponent;
import br.com.oi.sgis.repository.DepartmentComponentRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DepartmentComponentServiceTest {

    @InjectMocks
    DepartmentComponentService departmentComponentService;

    @Mock
    DepartmentComponentRepository departmentComponentRepository;

    @Test
    void listByDeparmentUserPaginated() {
        List<DepartmentComponent> departmentComponents = new EasyRandom()
                .objects(DepartmentComponent.class, 2).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("department"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<DepartmentComponent> pagedResult = new PageImpl<>(departmentComponents, paging, departmentComponents.size());

        Mockito.doReturn(pagedResult).when(departmentComponentRepository)
                .findByDepartment(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<DepartmentComponentDTO> departmentComponentToReturn =
                departmentComponentService.listByDeparmentUserPaginated(0, 10, List.of("department"), List.of());

        assertNotNull(departmentComponentToReturn);
        assertEquals(departmentComponents.size(), departmentComponentToReturn.getData().size());
    }

    @Test
    void listByDeparmentUserPaginatedEmpty() {
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<DepartmentComponent> pagedResult = new PageImpl<>(List.of(), paging, 0);

        Mockito.doReturn(pagedResult).when(departmentComponentRepository)
                .findByDepartment(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<DepartmentComponentDTO> departmentComponentToReturn =
                departmentComponentService.listByDeparmentUserPaginated(0, 10, Mockito.anyList(), Mockito.anyList());

        assert(departmentComponentToReturn.getData().isEmpty());
    }
}