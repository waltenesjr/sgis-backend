package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ParameterDTO;
import br.com.oi.sgis.entity.Parameter;
import br.com.oi.sgis.exception.ParameterNotFoundException;
import br.com.oi.sgis.repository.ParameterRepository;
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
class ParameterServiceTest {
    @InjectMocks
    private ParameterService parameterService;

    @Mock
    private ParameterRepository parameterRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listAllPaginated(){
        List<Parameter> parameters = new EasyRandom().objects(Parameter.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Parameter> pagedResult = new PageImpl<>(parameters, paging, parameters.size());

        Mockito.doReturn(pagedResult).when(parameterRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<ParameterDTO> parametersToReturn = parameterService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(parameters.size(), parametersToReturn.getData().size());
    }

    @Test
    void shouldListAllParametersWithoutTerm(){
        List<Parameter> parameters = new EasyRandom().objects(Parameter.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Parameter> pagedResult = new PageImpl<>(parameters, paging, parameters.size());

        Mockito.doReturn(pagedResult).when(parameterRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<ParameterDTO> parametersToReturn = parameterService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(parameters.size(), parametersToReturn.getData().size());
    }

    @Test
    void findById() throws ParameterNotFoundException {
        Parameter parameter = new EasyRandom().nextObject(Parameter.class);
        Mockito.doReturn(Optional.of(parameter)).when(parameterRepository).findById(Mockito.anyString());

        ParameterDTO parameterDTO = parameterService.findById(parameter.getId());

        assertEquals(parameter.getId(), parameterDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Parameter parameter = new EasyRandom().nextObject(Parameter.class);
        Mockito.doReturn(Optional.empty()).when(parameterRepository).findById(Mockito.anyString());
        Assertions.assertThrows(ParameterNotFoundException.class, () -> parameterService.findById(parameter.getId()));
    }

    @Test
    void createParameter() {
        ParameterDTO parameterDTO = new EasyRandom().nextObject(ParameterDTO.class);
        Mockito.doReturn(Optional.empty()).when(parameterRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = parameterService.createParameter(parameterDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createParameterExistsException() {
        Parameter parameter = new EasyRandom().nextObject(Parameter.class);
        ParameterDTO parameterDTO = new EasyRandom().nextObject(ParameterDTO.class);
        Mockito.doReturn(Optional.of(parameter)).when(parameterRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> parameterService.createParameter(parameterDTO));
        assertEquals(MessageUtils.PARAMETER_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createParameterException() {
        ParameterDTO parameterDTO = new EasyRandom().nextObject(ParameterDTO.class);
        Mockito.doReturn(Optional.empty()).when(parameterRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(parameterRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> parameterService.createParameter(parameterDTO));
        assertEquals(MessageUtils.PARAMETER_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateParameter() throws ParameterNotFoundException {
        Parameter parameter = new EasyRandom().nextObject(Parameter.class);
        ParameterDTO parameterDTO = new EasyRandom().nextObject(ParameterDTO.class);
        Mockito.doReturn(Optional.of(parameter)).when(parameterRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = parameterService.updateParameter(parameterDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateParameterException(){
        Parameter parameter = new EasyRandom().nextObject(Parameter.class);
        ParameterDTO parameterDTO = new EasyRandom().nextObject(ParameterDTO.class);
        Mockito.doReturn(Optional.of(parameter)).when(parameterRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(parameterRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> parameterService.updateParameter(parameterDTO));
        assertEquals(MessageUtils.PARAMETER_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void parameterReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).fillParameterReport(Mockito.any(), Mockito.any());
        Parameter parameter = new EasyRandom().nextObject(Parameter.class);
        List<Parameter> parameters = List.of(parameter, parameter);
        Pageable paging = PageRequest.of(0, 10);
        Page<Parameter> pagedResult = new PageImpl<>(parameters, paging, parameters.size());
        Mockito.doReturn(pagedResult).when(parameterRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = parameterService.parameterReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void parameterReportEmpty(){
        List<Parameter> parameters = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Parameter> pagedResult = new PageImpl<>(parameters, paging, parameters.size());
        Mockito.doReturn(pagedResult).when(parameterRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> parameterService.parameterReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws ParameterNotFoundException {
        Parameter parameter = new EasyRandom().nextObject(Parameter.class);
        Mockito.doReturn(Optional.of(parameter)).when(parameterRepository).findById(Mockito.any());
        parameterService.deleteById(parameter.getId());
        Mockito.verify(parameterRepository, Mockito.times(1)).deleteById(parameter.getId());
    }

    @Test
    void deleteByIdException()  {
        Parameter parameter = new EasyRandom().nextObject(Parameter.class);
        Mockito.doReturn(Optional.of(parameter)).when(parameterRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(parameterRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> parameterService.deleteById("1"));
        assertEquals(MessageUtils.PARAMETER_DELETE_ERROR.getDescription(), e.getMessage());
    }
}