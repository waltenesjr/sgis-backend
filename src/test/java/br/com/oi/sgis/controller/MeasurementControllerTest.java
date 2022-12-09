package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MeasurementDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.MeasurementNotFoundException;
import br.com.oi.sgis.service.MeasurementService;
import br.com.oi.sgis.util.PageableUtil;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MeasurementControllerTest {
    @InjectMocks
    private MeasurementController measurementController;

    @Mock
    private MeasurementService measurementService;

    @Test
    void listAll() {
        List<MeasurementDTO> measurements = new EasyRandom().objects(MeasurementDTO.class, 5).collect(Collectors.toList());

        Mockito.doReturn(measurements).when(measurementService).listAll();
        List<MeasurementDTO> measurementDTOSToReturn = measurementController.listAll();
        assertEquals(measurements.size(), measurementDTOSToReturn.size());
    }

    @Test
    void listAllWithSearch() {
        List<MeasurementDTO> measurementDTOS = new EasyRandom().objects(MeasurementDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<MeasurementDTO> expectedResponse = PageableUtil.paginate(new PageImpl(measurementDTOS, paging, measurementDTOS.size()));

        Mockito.doReturn(expectedResponse).when(measurementService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<MeasurementDTO>> response = measurementController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws MeasurementNotFoundException {
        MeasurementDTO measurementDTO = new EasyRandom().nextObject(MeasurementDTO.class);
        Mockito.doReturn(measurementDTO).when(measurementService).findById(Mockito.any());
        MeasurementDTO measurementDTOToReturn = measurementController.findById("1L");

        assertEquals(measurementDTO.getId(), measurementDTOToReturn.getId());
    }

    @Test
    void createMeasurement() {
        MeasurementDTO measurementDTO = new EasyRandom().nextObject(MeasurementDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(measurementService).createMeasurement(Mockito.any());
        MessageResponseDTO returnedResponse = measurementController.createMeasurement(measurementDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateMeasurement() throws MeasurementNotFoundException {
        MeasurementDTO measurementDTO = new EasyRandom().nextObject(MeasurementDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(measurementService).updateMeasurement(Mockito.any());
        MessageResponseDTO returnedResponse = measurementController.updateMeasurement(measurementDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(measurementService).measurementReport(Mockito.any(), Mockito.any(), Mockito.any());
        MeasurementDTO measurementDTO = new EasyRandom().nextObject(MeasurementDTO.class);
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = measurementController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws MeasurementNotFoundException {
        measurementController.deleteById("1");
        Mockito.verify(measurementService, Mockito.times(1)).deleteById("1");
    }
}