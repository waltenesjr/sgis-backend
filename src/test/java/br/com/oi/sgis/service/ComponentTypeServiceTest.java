package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ComponentTypeDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.ComponentType;
import br.com.oi.sgis.exception.ComponentTypeNotFoundException;
import br.com.oi.sgis.repository.ComponentTypeRepository;
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
class ComponentTypeServiceTest {
    @InjectMocks
    private ComponentTypeService componentTypeService;

    @Mock
    private ComponentTypeRepository componentTypeRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listAllPaginated(){
        List<ComponentType> componentTypes = new EasyRandom().objects(ComponentType.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ComponentType> pagedResult = new PageImpl<>(componentTypes, paging, componentTypes.size());

        Mockito.doReturn(pagedResult).when(componentTypeRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<ComponentTypeDTO> componentTypesToReturn = componentTypeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(componentTypes.size(), componentTypesToReturn.getData().size());
    }

    @Test
    void shouldListAllComponentTypesWithoutTerm(){
        List<ComponentType> componentTypes = new EasyRandom().objects(ComponentType.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ComponentType> pagedResult = new PageImpl<>(componentTypes, paging, componentTypes.size());

        Mockito.doReturn(pagedResult).when(componentTypeRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<ComponentTypeDTO> componentTypesToReturn = componentTypeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(componentTypes.size(), componentTypesToReturn.getData().size());
    }

    @Test
    void findById() throws ComponentTypeNotFoundException {
        ComponentType componentType = new EasyRandom().nextObject(ComponentType.class);
        Mockito.doReturn(Optional.of(componentType)).when(componentTypeRepository).findById(Mockito.anyString());

        ComponentTypeDTO componentTypeDTO = componentTypeService.findById(componentType.getId());

        assertEquals(componentType.getId(), componentTypeDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        ComponentType componentType = new EasyRandom().nextObject(ComponentType.class);
        Mockito.doReturn(Optional.empty()).when(componentTypeRepository).findById(Mockito.anyString());
        Assertions.assertThrows(ComponentTypeNotFoundException.class, () -> componentTypeService.findById(componentType.getId()));
    }

    @Test
    void createComponentType() {
        ComponentTypeDTO componentTypeDTO = ComponentTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(componentTypeRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = componentTypeService.createComponentType(componentTypeDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createComponentTypeExistsException() {
        ComponentType componentType = new EasyRandom().nextObject(ComponentType.class);
        ComponentTypeDTO componentTypeDTO = ComponentTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(componentType)).when(componentTypeRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> componentTypeService.createComponentType(componentTypeDTO));
        assertEquals(MessageUtils.COMPONENT_TYPE_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createComponentTypeException() {
        ComponentTypeDTO componentTypeDTO = ComponentTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(componentTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(componentTypeRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> componentTypeService.createComponentType(componentTypeDTO));
        assertEquals(MessageUtils.COMPONENT_TYPE_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateComponentType() throws ComponentTypeNotFoundException {
        ComponentType componentType = new EasyRandom().nextObject(ComponentType.class);
        ComponentTypeDTO componentTypeDTO = ComponentTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(componentType)).when(componentTypeRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = componentTypeService.updateComponentType(componentTypeDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateComponentTypeException(){
        ComponentType componentType = new EasyRandom().nextObject(ComponentType.class);
        ComponentTypeDTO componentTypeDTO = ComponentTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(componentType)).when(componentTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(componentTypeRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> componentTypeService.updateComponentType(componentTypeDTO));
        assertEquals(MessageUtils.COMPONENT_TYPE_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void componentTypeReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        ComponentType componentType = ComponentType.builder().id("1").description("Teste").build();
        List<ComponentType> componentTypes = List.of(componentType, componentType);
        Pageable paging = PageRequest.of(0, 10);
        Page<ComponentType> pagedResult = new PageImpl<>(componentTypes, paging, componentTypes.size());
        Mockito.doReturn(pagedResult).when(componentTypeRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = componentTypeService.componentTypeReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void componentTypeReportEmpty(){
        List<ComponentType> componentTypes = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<ComponentType> pagedResult = new PageImpl<>(componentTypes, paging, componentTypes.size());
        Mockito.doReturn(pagedResult).when(componentTypeRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> componentTypeService.componentTypeReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws ComponentTypeNotFoundException {
        ComponentType componentType = new EasyRandom().nextObject(ComponentType.class);
        Mockito.doReturn(Optional.of(componentType)).when(componentTypeRepository).findById(Mockito.any());
        componentTypeService.deleteById(componentType.getId());
        Mockito.verify(componentTypeRepository, Mockito.times(1)).deleteById(componentType.getId());
    }

    @Test
    void deleteByIdException()  {
        ComponentType componentType = new EasyRandom().nextObject(ComponentType.class);
        Mockito.doReturn(Optional.of(componentType)).when(componentTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(componentTypeRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> componentTypeService.deleteById("1"));
        assertEquals(MessageUtils.COMPONENT_TYPE_DELETE_ERROR.getDescription(), e.getMessage());
    }
}