package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.CompanyModel;
import br.com.oi.sgis.exception.CompanyModelNotFoundException;
import br.com.oi.sgis.repository.CompanyModelRepository;
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
class CompanyModelServiceTest {
    @InjectMocks
    private CompanyModelService companyModelService;

    @Mock
    private CompanyModelRepository companyModelRepository;
    @Mock
    private ReportService reportService;
    @Mock
    private CompanyService companyService;
    @Mock
    private AreaEquipamentService areaEquipamentService;
    @Test
    void listAllPaginated(){
        List<CompanyModel> companyModels = new EasyRandom().objects(CompanyModel.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<CompanyModel> pagedResult = new PageImpl<>(companyModels, paging, companyModels.size());

        Mockito.doReturn(pagedResult).when(companyModelRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<CompanyModelDTO> companyModelsToReturn = companyModelService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(companyModels.size(), companyModelsToReturn.getData().size());
    }

    @Test
    void shouldListAllCompanyModelsWithoutTerm(){
        List<CompanyModel> companyModels = new EasyRandom().objects(CompanyModel.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<CompanyModel> pagedResult = new PageImpl<>(companyModels, paging, companyModels.size());

        Mockito.doReturn(pagedResult).when(companyModelRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<CompanyModelDTO> companyModelsToReturn = companyModelService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(companyModels.size(), companyModelsToReturn.getData().size());
    }

    @Test
    void findById() throws CompanyModelNotFoundException {
        CompanyModel companyModel = new EasyRandom().nextObject(CompanyModel.class);
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);

        Mockito.doReturn(Optional.of(companyModel)).when(companyModelRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        CompanyModelDTO returnedCompanyModel = companyModelService.findByIdDTO(companyModelDTO);

        assertEquals(companyModel.getId().getCompany().getId(), returnedCompanyModel.getCompany().getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);

        Mockito.doReturn(Optional.empty()).when(companyModelRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        Assertions.assertThrows(CompanyModelNotFoundException.class, () -> companyModelService.findByIdDTO(companyModelDTO));
    }

    @Test @SneakyThrows
    void createCompanyModel() {
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);
        Mockito.doReturn(Optional.empty()).when(companyModelRepository).findById(Mockito.any());
        Mockito.doReturn(CompanyDTO.builder().build()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(AreaEquipamentDTO.builder().build()).when(areaEquipamentService).findById(Mockito.any());
        MessageResponseDTO responseDTO = companyModelService.createCompanyModel(companyModelDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createCompanyModelExistsException() {
        CompanyModel companyModel = new EasyRandom().nextObject(CompanyModel.class);
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);
        Mockito.doReturn(Optional.of(companyModel)).when(companyModelRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> companyModelService.createCompanyModel(companyModelDTO));
        assertEquals(MessageUtils.COMPANY_MODEL_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void createCompanyModelException() {
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);
        Mockito.doReturn(Optional.empty()).when(companyModelRepository).findById(Mockito.any());
        Mockito.doReturn(CompanyDTO.builder().build()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(AreaEquipamentDTO.builder().build()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(companyModelRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> companyModelService.createCompanyModel(companyModelDTO));
        assertEquals(MessageUtils.COMPANY_MODEL_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void updateCompanyModel()  {
        CompanyModel companyModel = new EasyRandom().nextObject(CompanyModel.class);
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);
        Mockito.doReturn(Optional.of(companyModel)).when(companyModelRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(CompanyDTO.builder().build()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(AreaEquipamentDTO.builder().build()).when(areaEquipamentService).findById(Mockito.any());
        MessageResponseDTO responseDTO = companyModelService.updateCompanyModel(companyModelDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test @SneakyThrows
    void updateCompanyModelException(){
        CompanyModel companyModel = new EasyRandom().nextObject(CompanyModel.class);
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);
        Mockito.doReturn(Optional.of(companyModel)).when(companyModelRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(CompanyDTO.builder().build()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(AreaEquipamentDTO.builder().build()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(companyModelRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> companyModelService.updateCompanyModel(companyModelDTO));
        assertEquals(MessageUtils.COMPANY_MODEL_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void companyModelReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReportThreeColumns(Mockito.any(), Mockito.any());
        CompanyModel companyModel = new EasyRandom().nextObject(CompanyModel.class);
        List<CompanyModel> companyModels = List.of(companyModel, companyModel);
        Pageable paging = PageRequest.of(0, 10);
        Page<CompanyModel> pagedResult = new PageImpl<>(companyModels, paging, companyModels.size());
        Mockito.doReturn(pagedResult).when(companyModelRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = companyModelService.companyModelReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void companyModelReportEmpty(){
        List<CompanyModel> companyModels = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<CompanyModel> pagedResult = new PageImpl<>(companyModels, paging, companyModels.size());
        Mockito.doReturn(pagedResult).when(companyModelRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> companyModelService.companyModelReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws CompanyModelNotFoundException {
        CompanyModel companyModel = new EasyRandom().nextObject(CompanyModel.class);
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);

        Mockito.doReturn(Optional.of(companyModel)).when(companyModelRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        companyModelService.deleteById(companyModelDTO);
        Mockito.verify(companyModelRepository, Mockito.times(1)).deleteById(Mockito.any());
    }

    @Test
    void deleteByIdException()  {
        CompanyModel companyModel = new EasyRandom().nextObject(CompanyModel.class);
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);

        Mockito.doReturn(Optional.of(companyModel)).when(companyModelRepository).findById(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(companyModelRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> companyModelService.deleteById(companyModelDTO));
        assertEquals(MessageUtils.COMPANY_MODEL_DELETE_ERROR.getDescription(), e.getMessage());
    }
}