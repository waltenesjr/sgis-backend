package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DefectDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Defect;
import br.com.oi.sgis.exception.DefectNotFoundException;
import br.com.oi.sgis.repository.DefectRepository;
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
class DefectServiceTest {

    @InjectMocks
    private DefectService defectService;
    @Mock
    private ReportService reportService;
    @Mock
    private DefectRepository defectRepository;

    @Test
    void listAll() {
        List<Defect> defects = new EasyRandom().objects(Defect.class, 5).collect(Collectors.toList());

        Mockito.doReturn(defects).when(defectRepository).findAll();
        List<DefectDTO> defectDTOSToReturn = defectService.listAll();
        assertEquals(defects.size(), defectDTOSToReturn.size());
    }

    @Test
    void listAllPaginated() {
        List<Defect> defects = new EasyRandom().objects(Defect.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Defect> pagedResult = new PageImpl<>(defects, paging, defects.size());

        Mockito.doReturn(pagedResult).when(defectRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<DefectDTO> defectsToReturn = defectService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(defects.size(), defectsToReturn.getData().size());
    }

    @Test
    void shouldListAllDefectsWithoutTerm(){
        List<Defect> defects = new EasyRandom().objects(Defect.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Defect> pagedResult = new PageImpl<>(defects, paging, defects.size());

        Mockito.doReturn(pagedResult).when(defectRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<DefectDTO> defectsToReturn = defectService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(defects.size(), defectsToReturn.getData().size());
    }

    @Test
    void findById() throws DefectNotFoundException {
        Defect defect = new EasyRandom().nextObject(Defect.class);
        Mockito.doReturn(Optional.of(defect)).when(defectRepository).findById(Mockito.anyString());
        DefectDTO defectDTO = defectService.findById(defect.getId());
        assertEquals(defect.getId(), defectDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Defect defect = new EasyRandom().nextObject(Defect.class);
        Mockito.doReturn(Optional.empty()).when(defectRepository).findById(Mockito.anyString());
        Assertions.assertThrows(DefectNotFoundException.class, () -> defectService.findById(defect.getId()));
    }

    @Test
    void createDefect() {
        DefectDTO defectDTO = DefectDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(defectRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = defectService.createDefect(defectDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createDefectExistsException() {
        Defect defect = new EasyRandom().nextObject(Defect.class);
        DefectDTO defectDTO = DefectDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(defect)).when(defectRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> defectService.createDefect(defectDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createDefectException() {
        DefectDTO defectDTO = DefectDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(defectRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(defectRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> defectService.createDefect(defectDTO));
        assertEquals(MessageUtils.DEFECT_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateDefect() throws DefectNotFoundException {
        Defect defect = new EasyRandom().nextObject(Defect.class);
        DefectDTO defectDTO = DefectDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(defect)).when(defectRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = defectService.updateDefect(defectDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void defectReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Defect defect = Defect.builder().id("1").description("Teste").build();
        List<Defect> defects = List.of(defect, defect);
        Pageable paging = PageRequest.of(0, 10);
        Page<Defect> pagedResult = new PageImpl<>(defects, paging, defects.size());
        Mockito.doReturn(pagedResult).when(defectRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = defectService.defectReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }
    @Test
    void defectReportEmpty(){
        List<Defect> defects = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Defect> pagedResult = new PageImpl<>(defects, paging, defects.size());
        Mockito.doReturn(pagedResult).when(defectRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> defectService.defectReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws DefectNotFoundException {
        Defect defect = new EasyRandom().nextObject(Defect.class);
        Mockito.doReturn(Optional.of(defect)).when(defectRepository).findById(Mockito.any());
        defectService.deleteById(defect.getId());
        Mockito.verify(defectRepository, Mockito.times(1)).deleteById(defect.getId());
    }

    @Test
    void deleteByIdException()  {
        Defect defect = new EasyRandom().nextObject(Defect.class);
        Mockito.doReturn(Optional.of(defect)).when(defectRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(defectRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> defectService.deleteById("1"));
        assertEquals(MessageUtils.DEFECT_DELETE_ERROR.getDescription(), e.getMessage());
    }
}