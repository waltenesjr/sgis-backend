package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.InstallationReasonEnum;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.exception.GenericQueryNotFoundException;
import br.com.oi.sgis.service.GenericQueryService;
import br.com.oi.sgis.util.PageableUtil;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.oi.sgis.util.MessageUtils.GENERIC_QUERY_CREATE_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericQueryControllerTest {

    @InjectMocks
    GenericQueryController queryController;

    @Mock
    GenericQueryService queryService;

    @Test
    void listAllPaginated() {
        List<GenericQueryDTO> dto = new EasyRandom().objects(GenericQueryDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("genericQueryType"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(dto, paging, dto.size()));

        doReturn(expectedResponse).when(queryService).findAllPaginated(any(), any(), anyList(), anyList(), anyString());

        ResponseEntity<PaginateResponseDTO<GenericQueryDTO>> response = queryController
                .listAllPaginated(0, 10, List.of("id"), List.of("genericQueryType"), "");

        assertNotNull(response.getBody());
        assertEquals(dto.get(0), response.getBody().getData().get(0));
        assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    void listAllPaginatedWithSearch() {
        List<GenericQueryDTO> dto = new EasyRandom().objects(GenericQueryDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("genericQueryType"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(dto, paging, dto.size()));

        doReturn(expectedResponse).when(queryService).findAllPaginated(any(), any(), anyList(), anyList(), anyString());

        ResponseEntity<PaginateResponseDTO<GenericQueryDTO>> response = queryController
                .listAllPaginated(0, 10, List.of("id"), List.of("genericQueryType"), "CNS");

        assertNotNull(response.getBody());
        assertEquals(dto.get(0), response.getBody().getData().get(0));
        assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createGenericQuery() {
        GenericQueryDTO dto = new EasyRandom().nextObject(GenericQueryDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();

        doReturn(responseDTO).when(queryService).create(any());

        MessageResponseDTO returnedResponse = queryController.createGenericQuery(dto);

        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateGenericQuery() throws GenericQueryNotFoundException {
        GenericQueryDTO dto = new EasyRandom().nextObject(GenericQueryDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();

        doReturn(responseDTO).when(queryService).update(any());

        MessageResponseDTO returnedResponse = queryController.updateGenericQuery(dto);

        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void delete() throws GenericQueryNotFoundException {
        Long id = 1L;

        doNothing().when(queryService).delete(any());

        queryController.delete(id);

        verify(queryService, times(1)).delete(any());
    }

    @Test
    void getById() throws GenericQueryNotFoundException{
        Long id = 1L;
        GenericQueryDTO dto = new EasyRandom().nextObject(GenericQueryDTO.class);
        dto.setId(id);

        doReturn(dto).when(queryService).findById(any());

        GenericQueryDTO response = queryController.getById(id);

        assertEquals(id, response.getId());
    }

    @Test
    void executeGenericQuery() throws JRException, IOException {
        byte[] report = new byte[50];
        GenericQueryDTO filterDTO = new EasyRandom().nextObject(GenericQueryDTO.class);
        Mockito.doReturn(report).when(queryService).executeQuery(Mockito.any(), Mockito.any());
        ResponseEntity<byte[]> responseReport = queryController.executeGenericQuery(filterDTO, TypeDocEnum.TXT);
        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void typeDoc() {
        List<ReporterOrderDTO> reasons = queryController.typeDoc();
        assertEquals(TypeDocEnum.txtXlsx().size(), reasons.size());
    }
}