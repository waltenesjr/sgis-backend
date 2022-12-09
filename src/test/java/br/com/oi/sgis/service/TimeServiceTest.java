package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.TimeDTO;
import br.com.oi.sgis.entity.Time;
import br.com.oi.sgis.exception.TimeNotFoundException;
import br.com.oi.sgis.repository.TimeRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class TimeServiceTest {
    @InjectMocks
    private TimeService timeService;

    @Mock
    private TimeRepository timeRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listAllPaginated(){
        List<Time> times = new EasyRandom().objects(Time.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Time> pagedResult = new PageImpl<>(times, paging, times.size());

        Mockito.doReturn(pagedResult).when(timeRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<TimeDTO> timesToReturn = timeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(times.size(), timesToReturn.getData().size());
    }

    @Test
    void shouldListAllTimesWithoutTerm(){
        List<Time> times = new EasyRandom().objects(Time.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Time> pagedResult = new PageImpl<>(times, paging, times.size());

        Mockito.doReturn(pagedResult).when(timeRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<TimeDTO> timesToReturn = timeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(times.size(), timesToReturn.getData().size());
    }

    @Test
    void findById() throws TimeNotFoundException {
        Time time = new EasyRandom().nextObject(Time.class);
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);
        Mockito.doReturn(Optional.of(time)).when(timeRepository).findById(Mockito.anyString(), Mockito.any());
        TimeDTO timeReturned = timeService.findById(timeDTO);

        assertEquals(time.getId().getIntervention().getId(), timeReturned.getIntervention().getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);
        Mockito.doReturn(Optional.empty()).when(timeRepository).findById(Mockito.anyString(), Mockito.anyString());
        Assertions.assertThrows(TimeNotFoundException.class, () -> timeService.findById(timeDTO));
    }

    @Test
    void createTime() {
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);
        Mockito.doReturn(Optional.empty()).when(timeRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = timeService.createTime(timeDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createTimeExistsException() {
        Time time = new EasyRandom().nextObject(Time.class);
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);
        Mockito.doReturn(Optional.of(time)).when(timeRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> timeService.createTime(timeDTO));
        assertEquals(MessageUtils.TIME_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createTimeException() {
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);
        Mockito.doReturn(Optional.empty()).when(timeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(timeRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> timeService.createTime(timeDTO));
        assertEquals(MessageUtils.TIME_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateTime() throws TimeNotFoundException {
        Time time = new EasyRandom().nextObject(Time.class);
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);
        Mockito.doReturn(Optional.of(time)).when(timeRepository).findById(Mockito.any(), Mockito.any());
        MessageResponseDTO responseDTO = timeService.updateTime(timeDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateTimeException(){
        Time time = new EasyRandom().nextObject(Time.class);
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);
        Mockito.doReturn(Optional.of(time)).when(timeRepository).findById(Mockito.any(), Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(timeRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> timeService.updateTime(timeDTO));
        assertEquals(MessageUtils.TIME_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void timeReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Time time = new EasyRandom().nextObject(Time.class);
        List<Time> times = List.of(time, time);
        Pageable paging = PageRequest.of(0, 10);
        Page<Time> pagedResult = new PageImpl<>(times, paging, times.size());
        Mockito.doReturn(pagedResult).when(timeRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = timeService.timeReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void timeReportEmpty(){
        List<Time> times = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Time> pagedResult = new PageImpl<>(times, paging, times.size());
        Mockito.doReturn(pagedResult).when(timeRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> timeService.timeReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws TimeNotFoundException {
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);
        Time time = new EasyRandom().nextObject(Time.class);
        Mockito.doReturn(Optional.of(time)).when(timeRepository).findById(Mockito.any(), Mockito.any());
        timeService.deleteById(timeDTO);
        Mockito.verify(timeRepository, Mockito.times(1)).deleteById(Mockito.any());
    }

    @Test
    void deleteByIdException()  {
        Time time = new EasyRandom().nextObject(Time.class);
        TimeDTO timeDTO = new EasyRandom().nextObject(TimeDTO.class);

        Mockito.doReturn(Optional.of(time)).when(timeRepository).findById(Mockito.any(), Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(timeRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> timeService.deleteById(timeDTO));
        assertEquals(MessageUtils.TIME_DELETE_ERROR.getDescription(), e.getMessage());
    }
}