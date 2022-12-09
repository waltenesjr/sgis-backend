package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.ElectricalProperty;
import br.com.oi.sgis.exception.EletricPropNotFoundException;
import br.com.oi.sgis.repository.ElectricalPropRepository;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
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
class ElectricalPropServiceTest {

    @InjectMocks
    private ElectricalPropService electricalPropService;

    @Mock
    private ElectricalPropRepository electricalPropRepository;
    @Mock
    private ReportService reportService;
    @Mock private UnityService unityService;

    @Test
    void listAll() {
        List<ElectricalProperty> propertyList = List.of(new EasyRandom().nextObject(ElectricalProperty.class));

        Mockito.doReturn(propertyList).when(electricalPropRepository).findAll();
        List<ElectricalPropDTO> electricalPropReturned = electricalPropService.listAll();
        assertNotNull(electricalPropReturned);
        assertEquals(propertyList.size(),electricalPropReturned.size());
    }

    @Test
    void findById() throws EletricPropNotFoundException {
        ElectricalProperty electricalProperty = new EasyRandom().nextObject(ElectricalProperty.class);
        Mockito.doReturn(Optional.of(electricalProperty)).when(electricalPropRepository).findById(Mockito.any());

        ElectricalPropDTO electricalPropReturned = electricalPropService.findById("HZ");
        assertEquals(electricalProperty.getId(), electricalPropReturned.getId());
    }

    @Test
    void findByIdWithException()   {
        Mockito.doReturn(Optional.empty()).when(electricalPropRepository).findById(Mockito.any());

        assertThrows(EletricPropNotFoundException.class, () -> electricalPropService.findById("HZ"));
    }

    @Test
    void listAllPaginated() {
        List<ElectricalProperty> electricalProps = new EasyRandom().objects(ElectricalProperty.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ElectricalProperty> pagedResult = new PageImpl<>(electricalProps, paging, electricalProps.size());

        Mockito.doReturn(pagedResult).when(electricalPropRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<ElectricalPropDTO> electricalPropsToReturn = electricalPropService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(electricalProps.size(), electricalPropsToReturn.getData().size());
    }

    @Test
    void shouldListAllElectricalPropertysWithoutTerm(){
        List<ElectricalProperty> electricalProps = new EasyRandom().objects(ElectricalProperty.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ElectricalProperty> pagedResult = new PageImpl<>(electricalProps, paging, electricalProps.size());

        Mockito.doReturn(pagedResult).when(electricalPropRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<ElectricalPropDTO> electricalPropsToReturn = electricalPropService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(electricalProps.size(), electricalPropsToReturn.getData().size());
    }

    @Test
    void createElectricalProperty() {
        ElectricalPropDTO electricalPropDTO = ElectricalPropDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(electricalPropRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = electricalPropService.createElectricalProperty(electricalPropDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createElectricalPropertyExistsException() {
        ElectricalProperty electricalProp = new EasyRandom().nextObject(ElectricalProperty.class);
        ElectricalPropDTO electricalPropDTO = ElectricalPropDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(electricalProp)).when(electricalPropRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> electricalPropService.createElectricalProperty(electricalPropDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createElectricalPropertyException() {
        ElectricalPropDTO electricalPropDTO = ElectricalPropDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(electricalPropRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(electricalPropRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> electricalPropService.createElectricalProperty(electricalPropDTO));
        assertEquals(MessageUtils.ELETRIC_PROPERTY_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateElectricalProperty() throws EletricPropNotFoundException {
        ElectricalProperty electricalProp = new EasyRandom().nextObject(ElectricalProperty.class);
        ElectricalPropDTO electricalPropDTO = ElectricalPropDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(electricalProp)).when(electricalPropRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = electricalPropService.updateElectricalProperty(electricalPropDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }


    @Test
    void updateElectricalPropertyException(){
        ElectricalProperty electricalProp = new EasyRandom().nextObject(ElectricalProperty.class);
        ElectricalPropDTO electricalPropDTO = ElectricalPropDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(electricalProp)).when(electricalPropRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(electricalPropRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> electricalPropService.updateElectricalProperty(electricalPropDTO));
        assertEquals(MessageUtils.ELETRIC_PROPERTY_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void electricalPropReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        ElectricalProperty electricalProp = ElectricalProperty.builder().id("1").description("Teste").build();
        List<ElectricalProperty> electricalProps = List.of(electricalProp, electricalProp);
        Pageable paging = PageRequest.of(0, 10);
        Page<ElectricalProperty> pagedResult = new PageImpl<>(electricalProps, paging, electricalProps.size());
        Mockito.doReturn(pagedResult).when(electricalPropRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = electricalPropService.electricalPropReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }
    @Test
    void electricalPropReportEmpty(){
        List<ElectricalProperty> electricalProps = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<ElectricalProperty> pagedResult = new PageImpl<>(electricalProps, paging, electricalProps.size());
        Mockito.doReturn(pagedResult).when(electricalPropRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> electricalPropService.electricalPropReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws EletricPropNotFoundException {
        ElectricalProperty electricalProp = new EasyRandom().nextObject(ElectricalProperty.class);
        Mockito.doReturn(Optional.of(electricalProp)).when(electricalPropRepository).findById(Mockito.any());
        electricalPropService.deleteById(electricalProp.getId());
        Mockito.verify(electricalPropRepository, Mockito.times(1)).deleteById(electricalProp.getId());
    }

    @Test
    void deleteByIdException()  {
        ElectricalProperty electricalProp = new EasyRandom().nextObject(ElectricalProperty.class);
        Mockito.doReturn(Optional.of(electricalProp)).when(electricalPropRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(electricalPropRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> electricalPropService.deleteById("1"));
        assertEquals(MessageUtils.ELETRIC_PROPERTY_DELETE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void physicalElectricalProperty() {
        ElectricalPropFilterDTO filterDto = new EasyRandom().nextObject(ElectricalPropFilterDTO.class);
        List<PhysicalElectricalPropsDTO> electricalProps = new EasyRandom().objects(PhysicalElectricalPropsDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<PhysicalElectricalPropsDTO> pagedResult = new PageImpl<>(electricalProps, paging, electricalProps.size());

        Mockito.doReturn(pagedResult).when(electricalPropRepository).findPhysicalElectricalProperties(Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<PhysicalElectricalPropsDTO> electricalPropsToReturn = electricalPropService.physicalElectricalProperty(0, 10, List.of("id"), List.of("date"), filterDto);
        assertEquals(electricalProps.size(), electricalPropsToReturn.getData().size());
    }

    @Test
    void physicalElectricalPropertyReport() throws JRException, IOException {
        ElectricalPropFilterDTO filterDto = new EasyRandom().nextObject(ElectricalPropFilterDTO.class);
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        List<PhysicalElectricalPropsDTO> electricalProps = new EasyRandom().objects(PhysicalElectricalPropsDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<PhysicalElectricalPropsDTO> pagedResult = new PageImpl<>(electricalProps, paging, electricalProps.size());
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).physicalElectricalPropertyReport(Mockito.any(), Mockito.any());
        Mockito.doReturn(pagedResult).when(electricalPropRepository).findPhysicalElectricalProperties(Mockito.any(), Mockito.any(Pageable.class));
        Mockito.doReturn(unityDTO).when(unityService).findById(Mockito.any());
        byte[] returnedReport = electricalPropService.physicalElectricalPropertyReport( List.of(), List.of(), filterDto);
        assertNotNull(returnedReport);
    }

    @Test
    void physicalElectricalPropertyReportEmpty()  {
        ElectricalPropFilterDTO filterDto = new EasyRandom().nextObject(ElectricalPropFilterDTO.class);
        List<PhysicalElectricalPropsDTO> electricalProps =List.of();
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<PhysicalElectricalPropsDTO> pagedResult = new PageImpl<>(electricalProps, paging, electricalProps.size());
        Mockito.doReturn(pagedResult).when(electricalPropRepository).findPhysicalElectricalProperties(Mockito.any(), Mockito.any(Pageable.class));
        Exception e = assertThrows(IllegalArgumentException.class, () ->  electricalPropService.physicalElectricalPropertyReport( List.of(), List.of(), filterDto));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }
}