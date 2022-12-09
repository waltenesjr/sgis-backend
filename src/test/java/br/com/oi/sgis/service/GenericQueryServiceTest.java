package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GenericQueryDTO;
import br.com.oi.sgis.dto.GenericQueryItemDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.GenericQuery;
import br.com.oi.sgis.entity.GenericQueryItem;
import br.com.oi.sgis.entity.TechnicalStaff;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.exception.GenericQueryNotFoundException;
import br.com.oi.sgis.repository.GenericQueryRepository;
import br.com.oi.sgis.service.factory.GenericQueryExecutionFactory;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.oi.sgis.util.MessageUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericQueryServiceTest {

    @InjectMocks
    private GenericQueryService queryService;
    @Mock
    private GenericQueryRepository queryRepository;
    @Mock
    private GenericQueryItemService queryItemService;
    @Mock
    private GenericQueryExecutionFactory executionQueryFactory;
    @Mock
    private ReportService reportService;

    @Test
    void shouldFindAllPaginatedWithoutSearch() {
        List<GenericQuery> queries = new EasyRandom().objects(GenericQuery.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("genericQueryType"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<GenericQuery> pagedResult = new PageImpl<>(queries, paging, queries.size());

        doReturn(pagedResult).when(queryRepository).findAll(any(Pageable.class));

        PaginateResponseDTO<GenericQueryDTO> queriesReturn = queryService
                .findAllPaginated(0, 10, List.of("id"), List.of("genericQueryType"), "");

        assertEquals(queries.size(), queriesReturn.getData().size());
    }

    @Test
    void shouldFindAllPaginatedWithSearch() {
        List<GenericQuery> queries = new EasyRandom().objects(GenericQuery.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("genericQueryType"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<GenericQuery> pagedResult = new PageImpl<>(queries, paging, queries.size());

        doReturn(pagedResult).when(queryRepository).findLike(any(Pageable.class), anyString());

        PaginateResponseDTO<GenericQueryDTO> queriesReturn = queryService
                .findAllPaginated(0, 10, List.of("id"), List.of("genericQueryType"), "CNS");

        assertEquals(queries.size(), queriesReturn.getData().size());
    }

    @Test
    void shouldCreateWithSuccess() {
        Long id = 1L;
        Long validId = id + 1;
        GenericQuery query = new EasyRandom().nextObject(GenericQuery.class);
        query.setId(id);
        GenericQueryDTO dto = new EasyRandom().nextObject(GenericQueryDTO.class);

        doReturn(query).when(queryRepository).findTopByOrderByIdDesc();
        doReturn(query).when(queryRepository).save(any());
        doNothing().when(queryItemService).save(any());

        MessageResponseDTO response = queryService.create(dto);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(GENERIC_QUERY_CREATE_SUCCESS.getDescription() + validId, response.getMessage());
    }

    @Test
    void shouldCreateThrowException() {
        GenericQuery query = new EasyRandom().nextObject(GenericQuery.class);
        GenericQueryDTO dto = new EasyRandom().nextObject(GenericQueryDTO.class);

        doReturn(query).when(queryRepository).findTopByOrderByIdDesc();
        doThrow(SqlScriptParserException.class).when(queryRepository).save(any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> queryService.create(dto));

        assertEquals(GENERIC_QUERY_CREATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void shouldUpdateWithSuccessWhenQueryItemIsEmptyInEntityAndDTO() throws GenericQueryNotFoundException {
        List<GenericQueryItem> queryItems = new ArrayList<>();
        List<GenericQueryItemDTO> queryItemsDTO = new ArrayList<>();
        GenericQuery query = new EasyRandom().nextObject(GenericQuery.class);
        query.setTechnicalStaff(getTechnicalStaff());
        query.setGenericQueryItems(queryItems);
        GenericQueryDTO dto = new EasyRandom().nextObject(GenericQueryDTO.class);
        dto.setColumns(queryItemsDTO);

        doReturn(Optional.of(query)).when(queryRepository).findById(any());
        doReturn(query).when(queryRepository).save(any());

        MessageResponseDTO response = queryService.update(dto);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(GENERIC_QUERY_UPDATE_SUCCESS.getDescription() + query.getId(), response.getMessage());
    }

    @Test
    void shouldUpdateWithSuccessWhenQueryItemIsEmptyInEntity() throws GenericQueryNotFoundException {
        List<GenericQueryItem> queryItems = new ArrayList<>();
        List<GenericQueryItemDTO> queryItemsDTO =
                new EasyRandom().objects(GenericQueryItemDTO.class, 5).collect(Collectors.toList());
        GenericQuery query = new EasyRandom().nextObject(GenericQuery.class);
        query.setTechnicalStaff(getTechnicalStaff());
        query.setGenericQueryItems(queryItems);
        GenericQueryDTO dto = new EasyRandom().nextObject(GenericQueryDTO.class);
        dto.setColumns(queryItemsDTO);

        doReturn(Optional.of(query)).when(queryRepository).findById(any());
        doReturn(query).when(queryRepository).save(any());
        doNothing().when(queryItemService).save(any());

        MessageResponseDTO response = queryService.update(dto);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(GENERIC_QUERY_UPDATE_SUCCESS.getDescription() + query.getId(), response.getMessage());
    }

    @Test
    void shouldUpdateWithSuccessWhenQueryItemIsEmptyInDTO() throws GenericQueryNotFoundException {
        List<GenericQueryItem> queryItems =
                new EasyRandom().objects(GenericQueryItem.class, 5).collect(Collectors.toList());
        List<GenericQueryItemDTO> queryItemsDTO = new ArrayList<>();
        GenericQuery query = new EasyRandom().nextObject(GenericQuery.class);
        query.setTechnicalStaff(getTechnicalStaff());
        query.setGenericQueryItems(queryItems);
        GenericQueryDTO dto = new EasyRandom().nextObject(GenericQueryDTO.class);
        dto.setColumns(queryItemsDTO);

        doReturn(Optional.of(query)).when(queryRepository).findById(any());
        doReturn(query).when(queryRepository).save(any());
        doNothing().when(queryItemService).delete(any());

        MessageResponseDTO response = queryService.update(dto);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(GENERIC_QUERY_UPDATE_SUCCESS.getDescription() + query.getId(), response.getMessage());
    }

    @Test
    void shouldUpdateWithSuccessWhenQueryItemIsNotEmptyInEntityAndDTO() throws GenericQueryNotFoundException {
        List<GenericQueryItem> queryItems =
                new EasyRandom().objects(GenericQueryItem.class, 5).collect(Collectors.toList());
        List<GenericQueryItemDTO> queryItemsDTO =
                new EasyRandom().objects(GenericQueryItemDTO.class, 5).collect(Collectors.toList());
        GenericQuery query = new EasyRandom().nextObject(GenericQuery.class);
        query.setTechnicalStaff(getTechnicalStaff());
        query.setGenericQueryItems(queryItems);
        GenericQueryDTO dto = new EasyRandom().nextObject(GenericQueryDTO.class);
        dto.setColumns(queryItemsDTO);

        doReturn(Optional.of(query)).when(queryRepository).findById(any());
        doReturn(query).when(queryRepository).save(any());
        doNothing().when(queryItemService).delete(any());
        doNothing().when(queryItemService).save(any());

        MessageResponseDTO response = queryService.update(dto);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(GENERIC_QUERY_UPDATE_SUCCESS.getDescription() + query.getId(), response.getMessage());
    }

    @Test
    void shouldUpdateThrowExceptionTechnicalNotEquals() {
        GenericQuery entity = new EasyRandom().nextObject(GenericQuery.class);
        GenericQueryDTO dto = new EasyRandom().nextObject(GenericQueryDTO.class);

        doReturn(Optional.of(entity)).when(queryRepository).findById(any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> queryService.update(dto));

        assertEquals(GENERIC_QUERY_UPDATE_DIFFERENT_USER_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void shouldUpdateThrowException(){
        GenericQuery entity = new EasyRandom().nextObject(GenericQuery.class);
        entity.setTechnicalStaff(getTechnicalStaff());
        GenericQueryDTO dto = new EasyRandom().nextObject(GenericQueryDTO.class);

        doReturn(Optional.of(entity)).when(queryRepository).findById(any());
        doThrow(SqlScriptParserException.class).when(queryRepository).save(any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> queryService.update(dto));

        assertEquals(GENERIC_QUERY_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void shouldDeleteWithSuccess() throws GenericQueryNotFoundException {
        Long id = 1L;
        GenericQuery entity = new EasyRandom().nextObject(GenericQuery.class);
        entity.setTechnicalStaff(getTechnicalStaff());

        doReturn(Optional.of(entity)).when(queryRepository).findById(any());
        doNothing().when(queryItemService).delete(any());

        queryService.delete(id);

        verify(queryRepository, times(1)).delete(any());
    }

    @Test
    void shouldDeleteThrowExceptionTechnicalNotEquals() {
        Long id = 1L;
        GenericQuery entity = new EasyRandom().nextObject(GenericQuery.class);

        doReturn(Optional.of(entity)).when(queryRepository).findById(any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> queryService.delete(id));

        assertEquals(GENERIC_QUERY_DELETE_DIFFERENT_USER_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void shouldDeleteThrowExceptionNotFound() {
        Long id = 0L;

        Exception e = assertThrows(GenericQueryNotFoundException.class, () -> queryService.delete(id));

        assertEquals(GENERIC_QUERY_NOT_FOUND_BY_ID.getDescription() + id, e.getMessage());
    }

    @Test
    void shouldDeleteThrowException(){
        Long id = 1L;
        GenericQuery entity = new EasyRandom().nextObject(GenericQuery.class);
        entity.setTechnicalStaff(getTechnicalStaff());

        doReturn(Optional.of(entity)).when(queryRepository).findById(any());
        doThrow(SqlScriptParserException.class).when(queryRepository).delete(any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> queryService.delete(id));

        assertEquals(GENERIC_QUERY_DELETE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void shouldFindByIdWithSuccess() throws GenericQueryNotFoundException {
        Long id = 1L;
        GenericQuery entity = new EasyRandom().nextObject(GenericQuery.class);
        entity.setId(id);

        doReturn(Optional.of(entity)).when(queryRepository).findById(any());

        GenericQueryDTO response = queryService.findById(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
    }

    @Test
    void shouldFindByIdThrowException(){
        Long id = 0L;
        Exception e = assertThrows(GenericQueryNotFoundException.class, () -> queryService.findById(id));
        assertEquals(GENERIC_QUERY_NOT_FOUND_BY_ID.getDescription() + id, e.getMessage());
    }

    private static TechnicalStaff getTechnicalStaff() {
        TechnicalStaff technical = new EasyRandom().nextObject(TechnicalStaff.class);
        technical.setId("RJ127925");
        return technical;
    }

    @Test
    void executeQuery() throws JRException, IOException {
        byte[] report = new byte[50];
        List<Object[]> resultQuery = new EasyRandom().objects(Object[].class, 5).collect(Collectors.toList());
        resultQuery.get(0)[0] = Date.valueOf(LocalDate.now());
        String sqlQuery = new EasyRandom().nextObject(String.class);
        GenericQueryDTO queryDTO = new EasyRandom().nextObject(GenericQueryDTO.class);
        queryDTO.setTotalizeFlag(true);
        Mockito.doReturn(report).when(reportService).genericQuery(any(),  any());
        Mockito.doReturn(sqlQuery).when(executionQueryFactory).createSqlQuery(any());
        Mockito.doReturn(resultQuery).when(queryRepository).executeQuery(any());

        byte[] reportReturned = queryService.executeQuery(queryDTO, TypeDocEnum.TXT) ;
        assertNotNull(reportReturned);
    }

    @Test
    void executeQuerySQLError() {
        List<Object[]> resultQuery = new EasyRandom().objects(Object[].class, 5).collect(Collectors.toList());
        resultQuery.get(0)[0] = Date.valueOf(LocalDate.now());
        GenericQueryDTO queryDTO = new EasyRandom().nextObject(GenericQueryDTO.class);
        queryDTO.setTotalizeFlag(true);
        Mockito.doThrow(NullPointerException.class).when(executionQueryFactory).createSqlQuery(any());
        Exception e = assertThrows(IllegalArgumentException.class,  ()->queryService.executeQuery(queryDTO, TypeDocEnum.TXT));
        assertEquals(MessageUtils.ERROR_GENERIC_QUERY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void executeQueryEmpty() {
        List<Object[]> resultQuery = List.of();
        String sqlQuery = new EasyRandom().nextObject(String.class);
        GenericQueryDTO queryDTO = new EasyRandom().nextObject(GenericQueryDTO.class);
        Mockito.doReturn(sqlQuery).when(executionQueryFactory).createSqlQuery(any());
        Mockito.doReturn(resultQuery).when(queryRepository).executeQuery(any());

        Exception e = assertThrows(IllegalArgumentException.class, ()->queryService.executeQuery(queryDTO, TypeDocEnum.TXT)) ;
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void executeQueryError() throws JRException, IOException {
        List<Object[]> resultQuery = new EasyRandom().objects(Object[].class, 5).collect(Collectors.toList());
        resultQuery.get(0)[0] = Date.valueOf(LocalDate.now());
        String sqlQuery = new EasyRandom().nextObject(String.class);
        GenericQueryDTO queryDTO = new EasyRandom().nextObject(GenericQueryDTO.class);
        Mockito.doReturn(sqlQuery).when(executionQueryFactory).createSqlQuery(any());
        Mockito.doReturn(resultQuery).when(queryRepository).executeQuery(any());

        Mockito.doThrow(JRException.class).when(reportService).genericQuery(any(),any());
        Exception e = assertThrows(IllegalArgumentException.class,  ()->queryService.executeQuery(queryDTO, TypeDocEnum.TXT));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }
}