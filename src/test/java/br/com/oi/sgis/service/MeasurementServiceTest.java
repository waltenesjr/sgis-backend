package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.MeasurementDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Measurement;
import br.com.oi.sgis.exception.MeasurementNotFoundException;
import br.com.oi.sgis.repository.MeasurementRepository;
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
class MeasurementServiceTest {
    @InjectMocks
    private MeasurementService measurementService;
    @Mock
    private ReportService reportService;
    @Mock
    private MeasurementRepository measurementRepository;

    @Test
    void listAll() {
        List<Measurement> measurements = new EasyRandom().objects(Measurement.class, 5).collect(Collectors.toList());

        Mockito.doReturn(measurements).when(measurementRepository).findAll();
        List<MeasurementDTO> measurementDTOSToReturn = measurementService.listAll();
        assertEquals(measurements.size(), measurementDTOSToReturn.size());
    }

    @Test
    void listAllPaginated() {
        List<Measurement> measurements = new EasyRandom().objects(Measurement.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Measurement> pagedResult = new PageImpl<>(measurements, paging, measurements.size());

        Mockito.doReturn(pagedResult).when(measurementRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<MeasurementDTO> measurementsToReturn = measurementService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(measurements.size(), measurementsToReturn.getData().size());
    }

    @Test
    void shouldListAllMeasurementsWithoutTerm(){
        List<Measurement> measurements = new EasyRandom().objects(Measurement.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Measurement> pagedResult = new PageImpl<>(measurements, paging, measurements.size());

        Mockito.doReturn(pagedResult).when(measurementRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<MeasurementDTO> measurementsToReturn = measurementService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(measurements.size(), measurementsToReturn.getData().size());
    }

    @Test
    void findById() throws MeasurementNotFoundException {
        Measurement measurement = new EasyRandom().nextObject(Measurement.class);
        Mockito.doReturn(Optional.of(measurement)).when(measurementRepository).findById(Mockito.anyString());
        MeasurementDTO measurementDTO = measurementService.findById(measurement.getId());
        assertEquals(measurement.getId(), measurementDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Measurement measurement = new EasyRandom().nextObject(Measurement.class);
        Mockito.doReturn(Optional.empty()).when(measurementRepository).findById(Mockito.anyString());
        Assertions.assertThrows(MeasurementNotFoundException.class, () -> measurementService.findById(measurement.getId()));
    }

    @Test
    void createMeasurement() {
        MeasurementDTO measurementDTO = MeasurementDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(measurementRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = measurementService.createMeasurement(measurementDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createMeasurementExistsException() {
        Measurement measurement = new EasyRandom().nextObject(Measurement.class);
        MeasurementDTO measurementDTO = MeasurementDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(measurement)).when(measurementRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> measurementService.createMeasurement(measurementDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createMeasurementException() {
        MeasurementDTO measurementDTO = MeasurementDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(measurementRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(measurementRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> measurementService.createMeasurement(measurementDTO));
        assertEquals(MessageUtils.MEASUREMENT_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateMeasurement() throws MeasurementNotFoundException {
        Measurement measurement = new EasyRandom().nextObject(Measurement.class);
        MeasurementDTO measurementDTO = MeasurementDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(measurement)).when(measurementRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = measurementService.updateMeasurement(measurementDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void updateMeasurementException(){
        Measurement measurement = new EasyRandom().nextObject(Measurement.class);
        MeasurementDTO measurementDTO = MeasurementDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(measurement)).when(measurementRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(measurementRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> measurementService.updateMeasurement(measurementDTO));
        assertEquals(MessageUtils.MEASUREMENT_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void measurementReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Measurement measurement = Measurement.builder().id("1").description("Teste").build();
        List<Measurement> measurements = List.of(measurement, measurement);
        Pageable paging = PageRequest.of(0, 10);
        Page<Measurement> pagedResult = new PageImpl<>(measurements, paging, measurements.size());
        Mockito.doReturn(pagedResult).when(measurementRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = measurementService.measurementReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }
    @Test
    void measurementReportEmpty(){
        List<Measurement> measurements = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Measurement> pagedResult = new PageImpl<>(measurements, paging, measurements.size());
        Mockito.doReturn(pagedResult).when(measurementRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> measurementService.measurementReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws MeasurementNotFoundException {
        Measurement measurement = new EasyRandom().nextObject(Measurement.class);
        Mockito.doReturn(Optional.of(measurement)).when(measurementRepository).findById(Mockito.any());
        measurementService.deleteById(measurement.getId());
        Mockito.verify(measurementRepository, Mockito.times(1)).deleteById(measurement.getId());
    }

    @Test
    void deleteByIdException()  {
        Measurement measurement = new EasyRandom().nextObject(Measurement.class);
        Mockito.doReturn(Optional.of(measurement)).when(measurementRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(measurementRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> measurementService.deleteById("1"));
        assertEquals(MessageUtils.MEASUREMENT_DELETE_ERROR.getDescription(), e.getMessage());
    }

}