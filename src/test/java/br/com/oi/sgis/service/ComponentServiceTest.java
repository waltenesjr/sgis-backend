package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ComponentDTO;
import br.com.oi.sgis.dto.ComponentTypeDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Component;
import br.com.oi.sgis.entity.ComponentType;
import br.com.oi.sgis.exception.ComponentNotFoundException;
import br.com.oi.sgis.repository.ComponentRepository;
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
class ComponentServiceTest {
    @InjectMocks
    private ComponentService componentService;
    @Mock
    private ComponentRepository componentRepository;
    @Mock
    private ComponentTypeRepository componentTypeRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listAllPaginated() {
        List<Component> components = new EasyRandom().objects(Component.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Component> pagedResult = new PageImpl<>(components, paging, components.size());

        Mockito.doReturn(pagedResult).when(componentRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<ComponentDTO> componentsToReturn = componentService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(components.size(), componentsToReturn.getData().size());
    }
    @Test
    void shouldListAllComponentsWithoutTerm(){
        List<Component> components = new EasyRandom().objects(Component.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Component> pagedResult = new PageImpl<>(components, paging, components.size());

        Mockito.doReturn(pagedResult).when(componentRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<ComponentDTO> componentsToReturn = componentService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(components.size(), componentsToReturn.getData().size());
    }
    @Test
    void findById() throws ComponentNotFoundException {
        Component component = new EasyRandom().nextObject(Component.class);
        Mockito.doReturn(Optional.of(component)).when(componentRepository).findById(Mockito.anyString());
        ComponentDTO componentDTO = componentService.findById(component.getId());
        assertEquals(component.getId(), componentDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Component component = new EasyRandom().nextObject(Component.class);
        Mockito.doReturn(Optional.empty()).when(componentRepository).findById(Mockito.anyString());
        Assertions.assertThrows(ComponentNotFoundException.class, () -> componentService.findById(component.getId()));
    }

    @Test
    void createComponent() throws ComponentNotFoundException {
        ComponentDTO componentDTO = new EasyRandom().nextObject(ComponentDTO.class);
        Mockito.doReturn(Optional.empty()).when(componentRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(new ComponentType())).when(componentTypeRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = componentService.createComponent(componentDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createComponentExistsException() {
        Component component = new EasyRandom().nextObject(Component.class);
        ComponentDTO componentDTO = new EasyRandom().nextObject(ComponentDTO.class);
        Mockito.doReturn(Optional.of(component)).when(componentRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> componentService.createComponent(componentDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createComponentException() {
        ComponentDTO componentDTO = new EasyRandom().nextObject(ComponentDTO.class);
        Mockito.doReturn(Optional.empty()).when(componentRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(new ComponentType())).when(componentTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(componentRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> componentService.createComponent(componentDTO));
        assertEquals(MessageUtils.COMPONENT_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateComponent() throws ComponentNotFoundException {
        Component component = new EasyRandom().nextObject(Component.class);
        ComponentDTO componentDTO = new EasyRandom().nextObject(ComponentDTO.class);
        Mockito.doReturn(Optional.of(new ComponentType())).when(componentTypeRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(component)).when(componentRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = componentService.updateComponent(componentDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void updateComponentWithException() {
        Component component = new EasyRandom().nextObject(Component.class);
        ComponentDTO componentDTO = new EasyRandom().nextObject(ComponentDTO.class);
        Mockito.doReturn(Optional.of(component)).when(componentRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(new ComponentType())).when(componentTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(componentRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> componentService.updateComponent(componentDTO));
        assertEquals(MessageUtils.COMPONENT_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void componentTypes() {
        ComponentType componentType = new EasyRandom().nextObject(ComponentType.class);
        Mockito.doReturn(List.of(componentType, componentType)).when(componentTypeRepository).findAll(Mockito.any(Sort.class));
        List<ComponentTypeDTO> returnComponentTypeList = componentService.componentTypes();
        assertNotNull(returnComponentTypeList);
        assertEquals(2,returnComponentTypeList.size());
    }

    @Test
    void componentReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Component component = Component.builder().id("1").description("Teste").build();
        List<Component> components = List.of(component, component);
        Pageable paging = PageRequest.of(0, 10);
        Page<Component> pagedResult = new PageImpl<>(components, paging, components.size());
        Mockito.doReturn(pagedResult).when(componentRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = componentService.componentReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void componentReportEmpty(){
        List<Component> components = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Component> pagedResult = new PageImpl<>(components, paging, components.size());
        Mockito.doReturn(pagedResult).when(componentRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> componentService.componentReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws ComponentNotFoundException {
        Component component = new EasyRandom().nextObject(Component.class);
        Mockito.doReturn(Optional.of(component)).when(componentRepository).findById(Mockito.any());
        componentService.deleteById(component.getId());
        Mockito.verify(componentRepository, Mockito.times(1)).deleteById(component.getId());
    }

    @Test
    void deleteByIdException() throws ComponentNotFoundException {
        Component component = new EasyRandom().nextObject(Component.class);
        Mockito.doReturn(Optional.of(component)).when(componentRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(componentRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> componentService.deleteById("1"));
        assertEquals(MessageUtils.COMPONENT_DELETE_ERROR.getDescription(), e.getMessage());
    }
}