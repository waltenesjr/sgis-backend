package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.EquipamentTypeDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.EquipamentType;
import br.com.oi.sgis.exception.EquipamentTypeNotFoundException;
import br.com.oi.sgis.repository.EquipamentTypeRepository;
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
class EquipamenteTypeServiceTest {

    @InjectMocks
    private EquipamentTypeService equipamentTypeService;

    @Mock
    private EquipamentTypeRepository equipamentTypeRepository;

    @Mock
    private ReportService reportService;

    @Test
    void shouldListAllApplicationsWithTerm(){
        List<EquipamentType> equipamentTypes = new EasyRandom().objects(EquipamentType.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<EquipamentType> pagedResult = new PageImpl<>(equipamentTypes, paging, equipamentTypes.size());

        Mockito.doReturn(pagedResult).when(equipamentTypeRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<EquipamentTypeDTO> equipamentTypesToReturn = equipamentTypeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        Assertions.assertEquals(equipamentTypes.size(), equipamentTypesToReturn.getData().size());
    }

    @Test
    void shouldListAllEquipamentTypesWithoutTerm(){
        List<EquipamentType> equipamentTypes = new EasyRandom().objects(EquipamentType.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<EquipamentType> pagedResult = new PageImpl<>(equipamentTypes, paging, equipamentTypes.size());

        Mockito.doReturn(pagedResult).when(equipamentTypeRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<EquipamentTypeDTO> equipamentTypesToReturn = equipamentTypeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        Assertions.assertEquals(equipamentTypes.size(), equipamentTypesToReturn.getData().size());
    }
    @Test
    void shouldFindById() throws EquipamentTypeNotFoundException {
        EquipamentType equipamentType = new EasyRandom().nextObject(EquipamentType.class);
        Mockito.doReturn(Optional.of(equipamentType)).when(equipamentTypeRepository).findById(Mockito.anyString());

        EquipamentTypeDTO equipamentTypeDTO = equipamentTypeService.findById(equipamentType.getId());

        Assertions.assertEquals(equipamentType.getId(), equipamentTypeDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        EquipamentType equipamentType = new EasyRandom().nextObject(EquipamentType.class);
        Mockito.doReturn(Optional.empty()).when(equipamentTypeRepository).findById(Mockito.anyString());
        Assertions.assertThrows(EquipamentTypeNotFoundException.class, () -> equipamentTypeService.findById(equipamentType.getId()));
    }

    @Test
    void createEquipamentType() {
        EquipamentTypeDTO equipamentTypeDTO = new EasyRandom().nextObject(EquipamentTypeDTO.class);
        Mockito.doReturn(Optional.empty()).when(equipamentTypeRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = equipamentTypeService.createEquipamentType(equipamentTypeDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createEquipamentTypeExistsException() {
        EquipamentType equipamentType = new EasyRandom().nextObject(EquipamentType.class);
        EquipamentTypeDTO equipamentTypeDTO = new EasyRandom().nextObject(EquipamentTypeDTO.class);
        Mockito.doReturn(Optional.of(equipamentType)).when(equipamentTypeRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> equipamentTypeService.createEquipamentType(equipamentTypeDTO));
        assertEquals(MessageUtils.EQUIPMENT_TYPE_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createEquipamentTypeException() {
        EquipamentTypeDTO equipamentTypeDTO = new EasyRandom().nextObject(EquipamentTypeDTO.class);
        Mockito.doReturn(Optional.empty()).when(equipamentTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(equipamentTypeRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> equipamentTypeService.createEquipamentType(equipamentTypeDTO));
        assertEquals(MessageUtils.EQUIPMENT_TYPE_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateEquipamentType() throws EquipamentTypeNotFoundException {
        EquipamentType equipamentType = new EasyRandom().nextObject(EquipamentType.class);
        EquipamentTypeDTO equipamentTypeDTO = new EasyRandom().nextObject(EquipamentTypeDTO.class);
        Mockito.doReturn(Optional.of(equipamentType)).when(equipamentTypeRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = equipamentTypeService.updateEquipamentType(equipamentTypeDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateEquipamentTypeException(){
        EquipamentType equipamentType = new EasyRandom().nextObject(EquipamentType.class);
        EquipamentTypeDTO equipamentTypeDTO = new EasyRandom().nextObject(EquipamentTypeDTO.class);
        Mockito.doReturn(Optional.of(equipamentType)).when(equipamentTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(equipamentTypeRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> equipamentTypeService.updateEquipamentType(equipamentTypeDTO));
        assertEquals(MessageUtils.EQUIPMENT_TYPE_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void equipamentTypeReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReportThreeColumns(Mockito.any(), Mockito.any());
        EquipamentType equipamentType = new EasyRandom().nextObject(EquipamentType.class);
        List<EquipamentType> equipamentTypes = List.of(equipamentType, equipamentType);
        Pageable paging = PageRequest.of(0, 10);
        Page<EquipamentType> pagedResult = new PageImpl<>(equipamentTypes, paging, equipamentTypes.size());
        Mockito.doReturn(pagedResult).when(equipamentTypeRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = equipamentTypeService.equipamentTypeReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void equipamentTypeReportEmpty(){
        List<EquipamentType> equipamentTypes = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<EquipamentType> pagedResult = new PageImpl<>(equipamentTypes, paging, equipamentTypes.size());
        Mockito.doReturn(pagedResult).when(equipamentTypeRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> equipamentTypeService.equipamentTypeReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws EquipamentTypeNotFoundException {
        EquipamentType equipamentType = new EasyRandom().nextObject(EquipamentType.class);
        Mockito.doReturn(Optional.of(equipamentType)).when(equipamentTypeRepository).findById(Mockito.any());
        equipamentTypeService.deleteById(equipamentType.getId());
        Mockito.verify(equipamentTypeRepository, Mockito.times(1)).deleteById(equipamentType.getId());
    }

    @Test
    void deleteByIdException()  {
        EquipamentType equipamentType = new EasyRandom().nextObject(EquipamentType.class);
        Mockito.doReturn(Optional.of(equipamentType)).when(equipamentTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(equipamentTypeRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> equipamentTypeService.deleteById("1"));
        assertEquals(MessageUtils.EQUIPMENT_TYPE_DELETE_ERROR.getDescription(), e.getMessage());
    }
}