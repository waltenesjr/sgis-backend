package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DiagnosisDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Diagnosis;
import br.com.oi.sgis.exception.DiagnosisNotFoundException;
import br.com.oi.sgis.repository.DiagnosisRepository;
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
class DiagnosisServiceTest {
    @InjectMocks
    private DiagnosisService diagnosisService;

    @Mock
    private DiagnosisRepository diagnosisRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listAllPaginated(){
        List<Diagnosis> diagnosiss = new EasyRandom().objects(Diagnosis.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Diagnosis> pagedResult = new PageImpl<>(diagnosiss, paging, diagnosiss.size());

        Mockito.doReturn(pagedResult).when(diagnosisRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<DiagnosisDTO> diagnosissToReturn = diagnosisService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(diagnosiss.size(), diagnosissToReturn.getData().size());
    }

    @Test
    void shouldListAllDiagnosissWithoutTerm(){
        List<Diagnosis> diagnosiss = new EasyRandom().objects(Diagnosis.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Diagnosis> pagedResult = new PageImpl<>(diagnosiss, paging, diagnosiss.size());

        Mockito.doReturn(pagedResult).when(diagnosisRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<DiagnosisDTO> diagnosissToReturn = diagnosisService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(diagnosiss.size(), diagnosissToReturn.getData().size());
    }

    @Test
    void findById() throws DiagnosisNotFoundException {
        Diagnosis diagnosis = new EasyRandom().nextObject(Diagnosis.class);
        Mockito.doReturn(Optional.of(diagnosis)).when(diagnosisRepository).findById(Mockito.anyString());

        DiagnosisDTO diagnosisDTO = diagnosisService.findById(diagnosis.getId());

        assertEquals(diagnosis.getId(), diagnosisDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Diagnosis diagnosis = new EasyRandom().nextObject(Diagnosis.class);
        Mockito.doReturn(Optional.empty()).when(diagnosisRepository).findById(Mockito.anyString());
        Assertions.assertThrows(DiagnosisNotFoundException.class, () -> diagnosisService.findById(diagnosis.getId()));
    }

    @Test
    void createDiagnosis() {
        DiagnosisDTO diagnosisDTO = DiagnosisDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(diagnosisRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = diagnosisService.createDiagnosis(diagnosisDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createDiagnosisExistsException() {
        Diagnosis diagnosis = new EasyRandom().nextObject(Diagnosis.class);
        DiagnosisDTO diagnosisDTO = DiagnosisDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(diagnosis)).when(diagnosisRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> diagnosisService.createDiagnosis(diagnosisDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createDiagnosisException() {
        DiagnosisDTO diagnosisDTO = DiagnosisDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(diagnosisRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(diagnosisRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> diagnosisService.createDiagnosis(diagnosisDTO));
        assertEquals(MessageUtils.DIAGNOSIS_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateDiagnosis() throws DiagnosisNotFoundException {
        Diagnosis diagnosis = new EasyRandom().nextObject(Diagnosis.class);
        DiagnosisDTO diagnosisDTO = DiagnosisDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(diagnosis)).when(diagnosisRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = diagnosisService.updateDiagnosis(diagnosisDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateDiagnosisException(){
        Diagnosis diagnosis = new EasyRandom().nextObject(Diagnosis.class);
        DiagnosisDTO diagnosisDTO = DiagnosisDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(diagnosis)).when(diagnosisRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(diagnosisRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> diagnosisService.updateDiagnosis(diagnosisDTO));
        assertEquals(MessageUtils.DIAGNOSIS_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void diagnosisReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Diagnosis diagnosis = Diagnosis.builder().id("1").description("Teste").build();
        List<Diagnosis> diagnoses = List.of(diagnosis, diagnosis);
        Pageable paging = PageRequest.of(0, 10);
        Page<Diagnosis> pagedResult = new PageImpl<>(diagnoses, paging, diagnoses.size());
        Mockito.doReturn(pagedResult).when(diagnosisRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = diagnosisService.diagnosisReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void diagnosisReportEmpty(){
        List<Diagnosis> diagnoses = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Diagnosis> pagedResult = new PageImpl<>(diagnoses, paging, diagnoses.size());
        Mockito.doReturn(pagedResult).when(diagnosisRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> diagnosisService.diagnosisReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws DiagnosisNotFoundException {
        Diagnosis diagnosis = new EasyRandom().nextObject(Diagnosis.class);
        Mockito.doReturn(Optional.of(diagnosis)).when(diagnosisRepository).findById(Mockito.any());
        diagnosisService.deleteById(diagnosis.getId());
        Mockito.verify(diagnosisRepository, Mockito.times(1)).deleteById(diagnosis.getId());
    }

    @Test
    void deleteByIdException()  {
        Diagnosis diagnosis = new EasyRandom().nextObject(Diagnosis.class);
        Mockito.doReturn(Optional.of(diagnosis)).when(diagnosisRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(diagnosisRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> diagnosisService.deleteById("1"));
        assertEquals(MessageUtils.DIAGNOSIS_DELETE_ERROR.getDescription(), e.getMessage());
    }
}