package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.InterventionDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Intervention;
import br.com.oi.sgis.exception.InterventionNotFoundException;
import br.com.oi.sgis.repository.InterventionRepository;
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
class InterventionServiceTest {
    @InjectMocks
    private InterventionService interventionService;

    @Mock
    private InterventionRepository interventionRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listAllPaginated(){
        List<Intervention> interventions = new EasyRandom().objects(Intervention.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Intervention> pagedResult = new PageImpl<>(interventions, paging, interventions.size());

        Mockito.doReturn(pagedResult).when(interventionRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<InterventionDTO> interventionsToReturn = interventionService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(interventions.size(), interventionsToReturn.getData().size());
    }

    @Test
    void shouldListAllInterventionsWithoutTerm(){
        List<Intervention> interventions = new EasyRandom().objects(Intervention.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Intervention> pagedResult = new PageImpl<>(interventions, paging, interventions.size());

        Mockito.doReturn(pagedResult).when(interventionRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<InterventionDTO> interventionsToReturn = interventionService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(interventions.size(), interventionsToReturn.getData().size());
    }

    @Test
    void findById() throws InterventionNotFoundException {
        Intervention intervention = new EasyRandom().nextObject(Intervention.class);
        Mockito.doReturn(Optional.of(intervention)).when(interventionRepository).findById(Mockito.anyString());

        InterventionDTO interventionDTO = interventionService.findById(intervention.getId());

        assertEquals(intervention.getId(), interventionDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Intervention intervention = new EasyRandom().nextObject(Intervention.class);
        Mockito.doReturn(Optional.empty()).when(interventionRepository).findById(Mockito.anyString());
        Assertions.assertThrows(InterventionNotFoundException.class, () -> interventionService.findById(intervention.getId()));
    }

    @Test
    void createIntervention() {
        InterventionDTO interventionDTO = InterventionDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(interventionRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = interventionService.createIntervention(interventionDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createInterventionExistsException() {
        Intervention intervention = new EasyRandom().nextObject(Intervention.class);
        InterventionDTO interventionDTO = InterventionDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(intervention)).when(interventionRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> interventionService.createIntervention(interventionDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createInterventionException() {
        InterventionDTO interventionDTO = InterventionDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(interventionRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(interventionRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> interventionService.createIntervention(interventionDTO));
        assertEquals(MessageUtils.INTERVENTION_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateIntervention() throws InterventionNotFoundException {
        Intervention intervention = new EasyRandom().nextObject(Intervention.class);
        InterventionDTO interventionDTO = InterventionDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(intervention)).when(interventionRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = interventionService.updateIntervention(interventionDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateInterventionException(){
        Intervention intervention = new EasyRandom().nextObject(Intervention.class);
        InterventionDTO interventionDTO = InterventionDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(intervention)).when(interventionRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(interventionRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> interventionService.updateIntervention(interventionDTO));
        assertEquals(MessageUtils.INTERVENTION_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void interventionReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Intervention intervention = Intervention.builder().id("1").description("Teste").build();
        List<Intervention> interventions = List.of(intervention, intervention);
        Pageable paging = PageRequest.of(0, 10);
        Page<Intervention> pagedResult = new PageImpl<>(interventions, paging, interventions.size());
        Mockito.doReturn(pagedResult).when(interventionRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = interventionService.interventionReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void interventionReportEmpty(){
        List<Intervention> interventions = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Intervention> pagedResult = new PageImpl<>(interventions, paging, interventions.size());
        Mockito.doReturn(pagedResult).when(interventionRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> interventionService.interventionReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws InterventionNotFoundException {
        Intervention intervention = new EasyRandom().nextObject(Intervention.class);
        Mockito.doReturn(Optional.of(intervention)).when(interventionRepository).findById(Mockito.any());
        interventionService.deleteById(intervention.getId());
        Mockito.verify(interventionRepository, Mockito.times(1)).deleteById(intervention.getId());
    }

    @Test
    void deleteByIdException()  {
        Intervention intervention = new EasyRandom().nextObject(Intervention.class);
        Mockito.doReturn(Optional.of(intervention)).when(interventionRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(interventionRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> interventionService.deleteById("1"));
        assertEquals(MessageUtils.INTERVENTION_DELETE_ERROR.getDescription(), e.getMessage());
    }

}