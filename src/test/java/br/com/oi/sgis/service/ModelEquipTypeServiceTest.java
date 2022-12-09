package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.ModelEquipamentType;
import br.com.oi.sgis.exception.*;
import br.com.oi.sgis.repository.ModelEquipTypeRepository;
import br.com.oi.sgis.util.MessageUtils;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
class ModelEquipTypeServiceTest {

    @InjectMocks
    private ModelEquipTypeService modelEquipTypeService;

    @Mock
    private ModelEquipTypeRepository modelEquipTypeRepository;
    @Mock
    private ReportService reportService;
    @Mock
    private TechnologyService technologyService;
    @Mock
    private TechnicalStaffService technicalStaffService;
    @Mock
    private CompanyService companyService;
    @Mock
    private EquipamentTypeService equipamentTypeService;

    private ModelEquipamentType modelEquipType;
    private ModelEquipTypeDTO modelEquipTypeDTO;
    @BeforeEach
    void setUp(){
         modelEquipType = new EasyRandom().nextObject(ModelEquipamentType.class);
        modelEquipTypeDTO = new EasyRandom().nextObject(ModelEquipTypeDTO.class);
    }

    @Test
    void listAllPaginated(){
        List<ModelEquipamentType> modelEquipamentTypes = new EasyRandom().objects(ModelEquipamentType.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ModelEquipamentType> pagedResult = new PageImpl<>(modelEquipamentTypes, paging, modelEquipamentTypes.size());

        Mockito.doReturn(pagedResult).when(modelEquipTypeRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<ModelEquipTypeDTO> modelEquipamentTypesToReturn = modelEquipTypeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        Assertions.assertEquals(modelEquipamentTypes.size(), modelEquipamentTypesToReturn.getData().size());
    }

    @Test
    void shouldListAllModelEquipamentTypesWithoutTerm(){
        List<ModelEquipamentType> modelEquipamentTypes = new EasyRandom().objects(ModelEquipamentType.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ModelEquipamentType> pagedResult = new PageImpl<>(modelEquipamentTypes, paging, modelEquipamentTypes.size());

        Mockito.doReturn(pagedResult).when(modelEquipTypeRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<ModelEquipTypeDTO> modelEquipamentTypesToReturn = modelEquipTypeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        Assertions.assertEquals(modelEquipamentTypes.size(), modelEquipamentTypesToReturn.getData().size());
    }

    @Test
    void findById() throws ModelEquipTypeNotFound {
        Mockito.doReturn(Optional.of(modelEquipType)).when(modelEquipTypeRepository).findById(Mockito.anyString());
        ModelEquipTypeDTO modelEquipTypeDTO = modelEquipTypeService.findById(modelEquipType.getId());
        Assertions.assertEquals(modelEquipType.getId(), modelEquipTypeDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Mockito.doReturn(Optional.empty()).when(modelEquipTypeRepository).findById(Mockito.anyString());
        Assertions.assertThrows(ModelEquipTypeNotFound.class, () -> modelEquipTypeService.findById(modelEquipType.getId()));
    }

    @Test @SneakyThrows
    void createModelEquipType() {
        Mockito.doReturn(Optional.empty()).when(modelEquipTypeRepository).findById(Mockito.any());
        mockValidations();
        MessageResponseDTO responseDTO = modelEquipTypeService.createModelEquipType(modelEquipTypeDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    private void mockValidations() throws TechnicalStaffNotFoundException, TechnologyNotFoundException, EquipamentTypeNotFoundException, CompanyNotFoundException {
        Mockito.doReturn(new TechnicalStaffDTO()).when(technicalStaffService).findById(Mockito.any());
        Mockito.doReturn(TechnologyDTO.builder().build()).when(technologyService).findById(Mockito.any());
        Mockito.doReturn(new EquipamentTypeDTO()).when(equipamentTypeService).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
    }

    @Test
    void createModelEquipTypeExistsException() {
        Mockito.doReturn(Optional.of(modelEquipType)).when(modelEquipTypeRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> modelEquipTypeService.createModelEquipType(modelEquipTypeDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void createModelEquipTypeException() {
        mockValidations();
        Mockito.doReturn(Optional.empty()).when(modelEquipTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(modelEquipTypeRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> modelEquipTypeService.createModelEquipType(modelEquipTypeDTO));
        assertEquals(MessageUtils.MODEL_EQUIPAMENT_TYPE_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void updateModelEquipType() throws ModelEquipTypeNotFound {
        mockValidations();
        Mockito.doReturn(Optional.of(modelEquipType)).when(modelEquipTypeRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = modelEquipTypeService.updateModelEquipType(modelEquipTypeDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test @SneakyThrows
    void updateModelEquipTypeException(){
        mockValidations();
        Mockito.doReturn(Optional.of(modelEquipType)).when(modelEquipTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(modelEquipTypeRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> modelEquipTypeService.updateModelEquipType(modelEquipTypeDTO));
        assertEquals(MessageUtils.MODEL_EQUIPAMENT_TYPE_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void modelEquipTypeReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).fillModelEquipamentReport(Mockito.any(), Mockito.any());
        List<ModelEquipamentType> modelEquipTypes = List.of(modelEquipType, modelEquipType);
        Pageable paging = PageRequest.of(0, 10);
        Page<ModelEquipamentType> pagedResult = new PageImpl<>(modelEquipTypes, paging, modelEquipTypes.size());
        Mockito.doReturn(pagedResult).when(modelEquipTypeRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = modelEquipTypeService.modelEquipTypeReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void modelEquipTypeReportEmpty(){
        List<ModelEquipamentType> modelEquipTypes = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<ModelEquipamentType> pagedResult = new PageImpl<>(modelEquipTypes, paging, modelEquipTypes.size());
        Mockito.doReturn(pagedResult).when(modelEquipTypeRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> modelEquipTypeService.modelEquipTypeReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws ModelEquipTypeNotFound {
        Mockito.doReturn(Optional.of(modelEquipType)).when(modelEquipTypeRepository).findById(Mockito.any());
        modelEquipTypeService.deleteById(modelEquipType.getId());
        Mockito.verify(modelEquipTypeRepository, Mockito.times(1)).deleteById(modelEquipType.getId());
    }

    @Test
    void deleteByIdException()  {
        Mockito.doReturn(Optional.of(modelEquipType)).when(modelEquipTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(modelEquipTypeRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> modelEquipTypeService.deleteById("1"));
        assertEquals(MessageUtils.MODEL_EQUIPAMENT_TYPE_DELETE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateDescontEquip() throws ModelEquipTypeNotFound {
        Mockito.doReturn(Optional.of(modelEquipType)).when(modelEquipTypeRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = modelEquipTypeService.updateDescontEquip(modelEquipTypeDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test @SneakyThrows
    void updateDescontEquipException(){
        Mockito.doReturn(Optional.of(modelEquipType)).when(modelEquipTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(modelEquipTypeRepository).updateEquipByModelDesct(Mockito.any(), Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> modelEquipTypeService.updateDescontEquip(modelEquipTypeDTO));
        assertEquals(MessageUtils.MODEL_EQUIPAMENT_TYPE_DESC_ERROR.getDescription(), e.getMessage());
    }
}