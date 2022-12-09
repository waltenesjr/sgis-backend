package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ComponentMovTypeDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.ComponentMovType;
import br.com.oi.sgis.exception.ComponentMovTypeNotFoundException;
import br.com.oi.sgis.repository.ComponentMovTypeRepository;
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
class ComponentMovTypeServiceTest {
    @InjectMocks
    private ComponentMovTypeService componentMovTypeService;

    @Mock
    private ComponentMovTypeRepository componentMovTypeRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listAllPaginated(){
        List<ComponentMovType> compnentMovTypes = new EasyRandom().objects(ComponentMovType.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ComponentMovType> pagedResult = new PageImpl<>(compnentMovTypes, paging, compnentMovTypes.size());

        Mockito.doReturn(pagedResult).when(componentMovTypeRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<ComponentMovTypeDTO> componentMovTypesToReturn = componentMovTypeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(compnentMovTypes.size(), componentMovTypesToReturn.getData().size());
    }

    @Test
    void shouldListAllComponentMovTypesWithoutTerm(){
        List<ComponentMovType> compnentMovTypes = new EasyRandom().objects(ComponentMovType.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ComponentMovType> pagedResult = new PageImpl<>(compnentMovTypes, paging, compnentMovTypes.size());

        Mockito.doReturn(pagedResult).when(componentMovTypeRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<ComponentMovTypeDTO> componentMovTypesToReturn = componentMovTypeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(compnentMovTypes.size(), componentMovTypesToReturn.getData().size());
    }

    @Test
    void findById() throws ComponentMovTypeNotFoundException {
        ComponentMovType componentMovType = new EasyRandom().nextObject(ComponentMovType.class);
        Mockito.doReturn(Optional.of(componentMovType)).when(componentMovTypeRepository).findById(Mockito.anyString());

        ComponentMovTypeDTO componentMovTypeDTO = componentMovTypeService.findById(componentMovType.getId());

        assertEquals(componentMovType.getId(), componentMovTypeDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        ComponentMovType componentMovType = new EasyRandom().nextObject(ComponentMovType.class);
        Mockito.doReturn(Optional.empty()).when(componentMovTypeRepository).findById(Mockito.anyString());
        Assertions.assertThrows(ComponentMovTypeNotFoundException.class, () -> componentMovTypeService.findById(componentMovType.getId()));
    }

    @Test
    void createComponentMovType() {
        ComponentMovTypeDTO componentMovTypeDTO = ComponentMovTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(componentMovTypeRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = componentMovTypeService.createComponentMovType(componentMovTypeDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createComponentMovTypeExistsException() {
        ComponentMovType componentMovType = new EasyRandom().nextObject(ComponentMovType.class);
        ComponentMovTypeDTO componentMovTypeDTO = ComponentMovTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(componentMovType)).when(componentMovTypeRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> componentMovTypeService.createComponentMovType(componentMovTypeDTO));
        assertEquals(MessageUtils.COMPONENT_MOV_TYPE_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createComponentMovTypeException() {
        ComponentMovTypeDTO componentMovTypeDTO = ComponentMovTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(componentMovTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(componentMovTypeRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> componentMovTypeService.createComponentMovType(componentMovTypeDTO));
        assertEquals(MessageUtils.COMPONENT_MOV_TYPE_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateComponentMovType() throws ComponentMovTypeNotFoundException {
        ComponentMovType componentMovType = new EasyRandom().nextObject(ComponentMovType.class);
        ComponentMovTypeDTO componentMovTypeDTO = ComponentMovTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(componentMovType)).when(componentMovTypeRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = componentMovTypeService.updateComponentMovType(componentMovTypeDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateComponentMovTypeException(){
        ComponentMovType componentMovType = new EasyRandom().nextObject(ComponentMovType.class);
        ComponentMovTypeDTO componentMovTypeDTO = ComponentMovTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(componentMovType)).when(componentMovTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(componentMovTypeRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> componentMovTypeService.updateComponentMovType(componentMovTypeDTO));
        assertEquals(MessageUtils.COMPONENT_MOV_TYPE_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void componentMovTypeReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        ComponentMovType componentMovType = ComponentMovType.builder().id("1").description("Teste").build();
        List<ComponentMovType> compnentMovTypes = List.of(componentMovType, componentMovType);
        Pageable paging = PageRequest.of(0, 10);
        Page<ComponentMovType> pagedResult = new PageImpl<>(compnentMovTypes, paging, compnentMovTypes.size());
        Mockito.doReturn(pagedResult).when(componentMovTypeRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = componentMovTypeService.componentMovTypeReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void componentMovTypeReportEmpty(){
        List<ComponentMovType> compnentMovTypes = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<ComponentMovType> pagedResult = new PageImpl<>(compnentMovTypes, paging, compnentMovTypes.size());
        Mockito.doReturn(pagedResult).when(componentMovTypeRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> componentMovTypeService.componentMovTypeReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws ComponentMovTypeNotFoundException {
        ComponentMovType componentMovType = new EasyRandom().nextObject(ComponentMovType.class);
        Mockito.doReturn(Optional.of(componentMovType)).when(componentMovTypeRepository).findById(Mockito.any());
        componentMovTypeService.deleteById(componentMovType.getId());
        Mockito.verify(componentMovTypeRepository, Mockito.times(1)).deleteById(componentMovType.getId());
    }

    @Test
    void deleteByIdException()  {
        ComponentMovType componentMovType = new EasyRandom().nextObject(ComponentMovType.class);
        Mockito.doReturn(Optional.of(componentMovType)).when(componentMovTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(componentMovTypeRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> componentMovTypeService.deleteById("1"));
        assertEquals(MessageUtils.COMPONENT_MOV_TYPE_DELETE_ERROR.getDescription(), e.getMessage());
    }
}