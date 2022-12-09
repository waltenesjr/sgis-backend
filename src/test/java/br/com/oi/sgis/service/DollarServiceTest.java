package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DollarDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Dollar;
import br.com.oi.sgis.exception.DollarNotFoundException;
import br.com.oi.sgis.repository.DollarRepository;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DollarServiceTest {
    @InjectMocks
    private DollarService dollarService;

    @Mock
    private DollarRepository dollarRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listAllPaginated(){
        List<Dollar> dollars = new EasyRandom().objects(Dollar.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Dollar> pagedResult = new PageImpl<>(dollars, paging, dollars.size());

        Mockito.doReturn(pagedResult).when(dollarRepository).findDollarByDateEquals(Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<DollarDTO> dollarsToReturn = dollarService.listAllPaginated(0, 10, List.of("id"), List.of("date"), null, LocalDateTime.now());
        assertEquals(dollars.size(), dollarsToReturn.getData().size());
    }

    @Test
    void listAllPaginatedByValue(){
        List<Dollar> dollars = new EasyRandom().objects(Dollar.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Dollar> pagedResult = new PageImpl<>(dollars, paging, dollars.size());

        Mockito.doReturn(pagedResult).when(dollarRepository).findDollarByValueEquals(Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<DollarDTO> dollarsToReturn = dollarService.listAllPaginated(0, 10, List.of("id"), List.of("date"), BigDecimal.ONE, null);
        assertEquals(dollars.size(), dollarsToReturn.getData().size());
    }

    @Test
    void shouldListAllDollarsWithoutTerm(){
        List<Dollar> dollars = new EasyRandom().objects(Dollar.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Dollar> pagedResult = new PageImpl<>(dollars, paging, dollars.size());

        Mockito.doReturn(pagedResult).when(dollarRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<DollarDTO> dollarsToReturn = dollarService.listAllPaginated(0, 10, List.of("id"), List.of("date"), null, null);
        assertEquals(dollars.size(), dollarsToReturn.getData().size());
    }

    @Test
    void findById() throws DollarNotFoundException {
        Dollar dollar = new EasyRandom().nextObject(Dollar.class);
        Mockito.doReturn(Optional.of(dollar)).when(dollarRepository).findById(Mockito.any());

        DollarDTO dollarDTO = dollarService.findById(dollar.getDate());

        assertEquals(dollar.getValue(), dollarDTO.getValue());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Dollar dollar = new EasyRandom().nextObject(Dollar.class);
        Mockito.doReturn(Optional.empty()).when(dollarRepository).findById(Mockito.any());
        Assertions.assertThrows(DollarNotFoundException.class, () -> dollarService.findById(dollar.getDate()));
    }

    @Test
    void createDollar() {
        DollarDTO dollarDTO = DollarDTO.builder().date(LocalDateTime.now()).value(BigDecimal.ONE).build();
        Mockito.doReturn(Optional.empty()).when(dollarRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = dollarService.createDollar(dollarDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createDollarExistsException() {
        Dollar dollar = new EasyRandom().nextObject(Dollar.class);
        DollarDTO dollarDTO = DollarDTO.builder().date(LocalDateTime.now()).value(BigDecimal.ONE).build();

        Mockito.doReturn(Optional.of(dollar)).when(dollarRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> dollarService.createDollar(dollarDTO));
        assertEquals(MessageUtils.DOLLAR_ALREADY_REGISTERED.getDescription(), e.getMessage());
    }

    @Test
    void createDollarException() {
        DollarDTO dollarDTO = DollarDTO.builder().date(LocalDateTime.now()).value(BigDecimal.ONE).build();
        Mockito.doReturn(Optional.empty()).when(dollarRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(dollarRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> dollarService.createDollar(dollarDTO));
        assertEquals(MessageUtils.DOLLAR_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateDollar() throws DollarNotFoundException {
        Dollar dollar = new EasyRandom().nextObject(Dollar.class);
        DollarDTO dollarDTO = DollarDTO.builder().date(LocalDateTime.now()).value(BigDecimal.ONE).build();

        Mockito.doReturn(Optional.of(dollar)).when(dollarRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = dollarService.updateDollar(dollarDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateDollarException(){
        Dollar dollar = new EasyRandom().nextObject(Dollar.class);
        DollarDTO dollarDTO = DollarDTO.builder().date(LocalDateTime.now()).value(BigDecimal.ONE).build();

        Mockito.doReturn(Optional.of(dollar)).when(dollarRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(dollarRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> dollarService.updateDollar(dollarDTO));
        assertEquals(MessageUtils.DOLLAR_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void dollarReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Dollar dollarDTO = Dollar.builder().date(LocalDateTime.now()).value(BigDecimal.ONE).build();
        List<Dollar> dollarDTOS = List.of(dollarDTO, dollarDTO);
        Pageable paging = PageRequest.of(0, 10);
        Page<Dollar> pagedResult = new PageImpl<>(dollarDTOS, paging, dollarDTOS.size());
        Mockito.doReturn(pagedResult).when(dollarRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = dollarService.dollarReport(null, null, List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void dollarReportEmpty(){
        List<Dollar> dollars = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Dollar> pagedResult = new PageImpl<>(dollars, paging, dollars.size());
        Mockito.doReturn(pagedResult).when(dollarRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> dollarService.dollarReport(null, null, null, null));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws DollarNotFoundException {
        Dollar dollar = new EasyRandom().nextObject(Dollar.class);
        Mockito.doReturn(Optional.of(dollar)).when(dollarRepository).findById(Mockito.any());
        dollarService.deleteById(dollar.getDate());
        Mockito.verify(dollarRepository, Mockito.times(1)).deleteById(dollar.getDate());
    }

    @Test
    void deleteByIdException()  {
        Dollar dollar = new EasyRandom().nextObject(Dollar.class);
        Mockito.doReturn(Optional.of(dollar)).when(dollarRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(dollarRepository).deleteById(Mockito.any());
        LocalDateTime date = LocalDateTime.now();
        Exception e = assertThrows(IllegalArgumentException.class, () -> dollarService.deleteById(date));
        assertEquals(MessageUtils.DOLLAR_DELETE_ERROR.getDescription(), e.getMessage());
    }

}