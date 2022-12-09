package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.TechnologyDTO;
import br.com.oi.sgis.entity.Technology;
import br.com.oi.sgis.exception.TechnologyNotFoundException;
import br.com.oi.sgis.repository.TechnologyRepository;
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
class TechnologyServiceTest
{
    @InjectMocks
    private TechnologyService technologyService;

    @Mock
    private TechnologyRepository technologyRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listAllPaginated(){
        List<Technology> technologies = new EasyRandom().objects(Technology.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Technology> pagedResult = new PageImpl<>(technologies, paging, technologies.size());

        Mockito.doReturn(pagedResult).when(technologyRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<TechnologyDTO> technologysToReturn = technologyService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(technologies.size(), technologysToReturn.getData().size());
    }

    @Test
    void shouldListAllTechnologysWithoutTerm(){
        List<Technology> technologies = new EasyRandom().objects(Technology.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Technology> pagedResult = new PageImpl<>(technologies, paging, technologies.size());

        Mockito.doReturn(pagedResult).when(technologyRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<TechnologyDTO> technologysToReturn = technologyService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(technologies.size(), technologysToReturn.getData().size());
    }

    @Test
    void findById() throws TechnologyNotFoundException {
        Technology technology = new EasyRandom().nextObject(Technology.class);
        Mockito.doReturn(Optional.of(technology)).when(technologyRepository).findById(Mockito.anyString());

        TechnologyDTO technologyDTO = technologyService.findById(technology.getId());

        assertEquals(technology.getId(), technologyDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Technology technology = new EasyRandom().nextObject(Technology.class);
        Mockito.doReturn(Optional.empty()).when(technologyRepository).findById(Mockito.anyString());
        Assertions.assertThrows(TechnologyNotFoundException.class, () -> technologyService.findById(technology.getId()));
    }

    @Test
    void createTechnology() {
        TechnologyDTO technologyDTO = TechnologyDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(technologyRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = technologyService.createTechnology(technologyDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createTechnologyExistsException() {
        Technology technology = new EasyRandom().nextObject(Technology.class);
        TechnologyDTO technologyDTO = TechnologyDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(technology)).when(technologyRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> technologyService.createTechnology(technologyDTO));
        assertEquals(MessageUtils.TECHNOLOGY_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createTechnologyException() {
        TechnologyDTO technologyDTO = TechnologyDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(technologyRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(technologyRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> technologyService.createTechnology(technologyDTO));
        assertEquals(MessageUtils.TECHNOLOGY_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateTechnology() throws TechnologyNotFoundException {
        Technology technology = new EasyRandom().nextObject(Technology.class);
        TechnologyDTO technologyDTO = TechnologyDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(technology)).when(technologyRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = technologyService.updateTechnology(technologyDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateTechnologyException(){
        Technology technology = new EasyRandom().nextObject(Technology.class);
        TechnologyDTO technologyDTO = TechnologyDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(technology)).when(technologyRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(technologyRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> technologyService.updateTechnology(technologyDTO));
        assertEquals(MessageUtils.TECHNOLOGY_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void technologyReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Technology technology = Technology.builder().id("1").description("Teste").build();
        List<Technology> technologies = List.of(technology, technology);
        Pageable paging = PageRequest.of(0, 10);
        Page<Technology> pagedResult = new PageImpl<>(technologies, paging, technologies.size());
        Mockito.doReturn(pagedResult).when(technologyRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = technologyService.technologyReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void technologyReportEmpty(){
        List<Technology> technologies = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Technology> pagedResult = new PageImpl<>(technologies, paging, technologies.size());
        Mockito.doReturn(pagedResult).when(technologyRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> technologyService.technologyReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws TechnologyNotFoundException {
        Technology technology = new EasyRandom().nextObject(Technology.class);
        Mockito.doReturn(Optional.of(technology)).when(technologyRepository).findById(Mockito.any());
        technologyService.deleteById(technology.getId());
        Mockito.verify(technologyRepository, Mockito.times(1)).deleteById(technology.getId());
    }

    @Test
    void deleteByIdException()  {
        Technology technology = new EasyRandom().nextObject(Technology.class);
        Mockito.doReturn(Optional.of(technology)).when(technologyRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(technologyRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> technologyService.deleteById("1"));
        assertEquals(MessageUtils.TECHNOLOGY_DELETE_ERROR.getDescription(), e.getMessage());
    }
}