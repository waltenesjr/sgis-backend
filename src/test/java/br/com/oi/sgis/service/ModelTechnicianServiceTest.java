package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.ModelTechnician;
import br.com.oi.sgis.exception.ModelTechnicianNotFoundException;
import br.com.oi.sgis.repository.ModelTechnicianRepository;
import br.com.oi.sgis.util.MessageUtils;
import lombok.SneakyThrows;
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

class ModelTechnicianServiceTest {
    @InjectMocks
    private ModelTechnicianService modelTechnicianService;

    @Mock
    private ModelTechnicianRepository modelTechnicianRepository;
    @Mock
    private ReportService reportService;
    @Mock
    private TechnicalStaffService technicalStaffService;
    @Mock
    private AreaEquipamentService areaEquipamentService;

    @Test
    void listAllPaginated(){
        List<ModelTechnician> modelTechnicians = new EasyRandom().objects(ModelTechnician.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ModelTechnician> pagedResult = new PageImpl<>(modelTechnicians, paging, modelTechnicians.size());

        Mockito.doReturn(pagedResult).when(modelTechnicianRepository).findLike(Mockito.anyString(),Mockito.any() ,Mockito.any(Pageable.class));
        PaginateResponseDTO<ModelTechnicianDTO> modelTechniciansToReturn = modelTechnicianService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(modelTechnicians.size(), modelTechniciansToReturn.getData().size());
    }

    @Test
    void shouldListAllModelTechniciansWithoutTerm(){
        List<ModelTechnician> modelTechnicians = new EasyRandom().objects(ModelTechnician.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<ModelTechnician> pagedResult = new PageImpl<>(modelTechnicians, paging, modelTechnicians.size());

        Mockito.doReturn(pagedResult).when(modelTechnicianRepository).findLike(Mockito.anyString(),Mockito.any() ,Mockito.any(Pageable.class));
        PaginateResponseDTO<ModelTechnicianDTO> modelTechniciansToReturn = modelTechnicianService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(modelTechnicians.size(), modelTechniciansToReturn.getData().size());
    }

    @Test
    void findById() throws ModelTechnicianNotFoundException {
        ModelTechnician modelTechnician = new EasyRandom().nextObject(ModelTechnician.class);
        ModelTechnicianDTO dto = new EasyRandom().nextObject(ModelTechnicianDTO.class);

        Mockito.doReturn(Optional.of(modelTechnician)).when(modelTechnicianRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        ModelTechnicianDTO modelTechnicianDTO = modelTechnicianService.findById(dto);
        assertEquals(modelTechnician.getId().getModel().getId(), modelTechnicianDTO.getModel().getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        ModelTechnicianDTO dto = new EasyRandom().nextObject(ModelTechnicianDTO.class);
        Mockito.doReturn(Optional.empty()).when(modelTechnicianRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        Assertions.assertThrows(ModelTechnicianNotFoundException.class, () -> modelTechnicianService.findById(dto));
    }

    @Test @SneakyThrows
    void createModelTechnician() {
        ModelTechnicianDTO modelTechnicianDTO = new EasyRandom().nextObject(ModelTechnicianDTO.class);
        Mockito.doReturn(Optional.empty()).when(modelTechnicianRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(AreaEquipamentDTO.builder().build()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doReturn(TechnicalStaffDTO.builder().build()).when(technicalStaffService).findById(Mockito.any());
        MessageResponseDTO responseDTO = modelTechnicianService.createModelTechnician(modelTechnicianDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createModelTechnicianExistsException() {
        ModelTechnician modelTechnician = new EasyRandom().nextObject(ModelTechnician.class);
        ModelTechnicianDTO modelTechnicianDTO = new EasyRandom().nextObject(ModelTechnicianDTO.class);
        Mockito.doReturn(Optional.of(modelTechnician)).when(modelTechnicianRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> modelTechnicianService.createModelTechnician(modelTechnicianDTO));
        assertEquals(MessageUtils.MODEL_TS_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void createModelTechnicianException() {
        ModelTechnicianDTO modelTechnicianDTO = new EasyRandom().nextObject(ModelTechnicianDTO.class);
        Mockito.doReturn(Optional.empty()).when(modelTechnicianRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(AreaEquipamentDTO.builder().build()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doReturn(TechnicalStaffDTO.builder().build()).when(technicalStaffService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(modelTechnicianRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> modelTechnicianService.createModelTechnician(modelTechnicianDTO));
        assertEquals(MessageUtils.MODEL_TS_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void updateModelTechnician() throws ModelTechnicianNotFoundException {
        ModelTechnicianDTO modelTechnicianDTO =new EasyRandom().nextObject(ModelTechnicianDTO.class);
        ModelTechnician modelTechnician = new EasyRandom().nextObject(ModelTechnician.class);

        Mockito.doReturn(Optional.of(modelTechnician)).when(modelTechnicianRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(AreaEquipamentDTO.builder().build()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doReturn(TechnicalStaffDTO.builder().build()).when(technicalStaffService).findById(Mockito.any());
        MessageResponseDTO responseDTO = modelTechnicianService.updateModelTechnician(modelTechnicianDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test @SneakyThrows
    void updateModelTechnicianException(){
        ModelTechnician modelTechnician = new EasyRandom().nextObject(ModelTechnician.class);
        ModelTechnicianDTO modelTechnicianDTO = new EasyRandom().nextObject(ModelTechnicianDTO.class);
        Mockito.doReturn(Optional.of(modelTechnician)).when(modelTechnicianRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(AreaEquipamentDTO.builder().build()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doReturn(TechnicalStaffDTO.builder().build()).when(technicalStaffService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(modelTechnicianRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> modelTechnicianService.updateModelTechnician(modelTechnicianDTO));
        assertEquals(MessageUtils.MODEL_TS_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void modelTechnicianReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        ModelTechnician modelTechnician = new EasyRandom().nextObject(ModelTechnician.class);
        List<ModelTechnician> modelTechnicians = List.of(modelTechnician, modelTechnician);
        Pageable paging = PageRequest.of(0, 10);
        Page<ModelTechnician> pagedResult = new PageImpl<>(modelTechnicians, paging, modelTechnicians.size());
        Mockito.doReturn(pagedResult).when(modelTechnicianRepository).findLike(Mockito.anyString(),Mockito.any() ,Mockito.any(Pageable.class));
        byte[] returnedReport = modelTechnicianService.modelTechnicianReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void modelTechnicianReportEmpty(){
        List<ModelTechnician> modelTechnicians = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<ModelTechnician> pagedResult = new PageImpl<>(modelTechnicians, paging, modelTechnicians.size());
        Mockito.doReturn(pagedResult).when(modelTechnicianRepository).findLike(Mockito.anyString(),Mockito.any() ,Mockito.any(Pageable.class));
        Exception e = assertThrows(IllegalArgumentException.class, () -> modelTechnicianService.modelTechnicianReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws ModelTechnicianNotFoundException {
        ModelTechnician modelTechnician = new EasyRandom().nextObject(ModelTechnician.class);
        ModelTechnicianDTO modelTechnicianDTO = new EasyRandom().nextObject(ModelTechnicianDTO.class);

        Mockito.doReturn(Optional.of(modelTechnician)).when(modelTechnicianRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        modelTechnicianService.deleteById(modelTechnicianDTO);
        Mockito.verify(modelTechnicianRepository, Mockito.times(1)).deleteById(Mockito.any());
    }

    @Test
    void deleteByIdException()  {
        ModelTechnician modelTechnician = new EasyRandom().nextObject(ModelTechnician.class);
        ModelTechnicianDTO modelTechnicianDTO = new EasyRandom().nextObject(ModelTechnicianDTO.class);

        Mockito.doReturn(Optional.of(modelTechnician)).when(modelTechnicianRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(modelTechnicianRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> modelTechnicianService.deleteById(modelTechnicianDTO));
        assertEquals(MessageUtils.MODEL_TS_DELETE_ERROR.getDescription(), e.getMessage());
    }
}