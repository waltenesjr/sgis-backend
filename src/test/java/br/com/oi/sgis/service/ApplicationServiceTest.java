package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ApplicationDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Application;
import br.com.oi.sgis.exception.ApplicationNotFoundException;
import br.com.oi.sgis.repository.ApplicationRepository;
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
class ApplicationServiceTest {

    @InjectMocks
    private ApplicationService applicationService;

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listAllPaginated(){
        List<Application> applications = new EasyRandom().objects(Application.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Application> pagedResult = new PageImpl<>(applications, paging, applications.size());

        Mockito.doReturn(pagedResult).when(applicationRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<ApplicationDTO> applicationsToReturn = applicationService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(applications.size(), applicationsToReturn.getData().size());
    }

    @Test
    void shouldListAllApplicationsWithoutTerm(){
        List<Application> applications = new EasyRandom().objects(Application.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Application> pagedResult = new PageImpl<>(applications, paging, applications.size());

        Mockito.doReturn(pagedResult).when(applicationRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<ApplicationDTO> applicationsToReturn = applicationService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(applications.size(), applicationsToReturn.getData().size());
    }

    @Test
    void findById() throws ApplicationNotFoundException {
        Application application = new EasyRandom().nextObject(Application.class);
        Mockito.doReturn(Optional.of(application)).when(applicationRepository).findById(Mockito.anyString());

        ApplicationDTO applicationDTO = applicationService.findById(application.getId());

        assertEquals(application.getId(), applicationDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Application application = new EasyRandom().nextObject(Application.class);
        Mockito.doReturn(Optional.empty()).when(applicationRepository).findById(Mockito.anyString());
        Assertions.assertThrows(ApplicationNotFoundException.class, () -> applicationService.findById(application.getId()));
    }

    @Test
    void createApplication() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(applicationRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = applicationService.createApplication(applicationDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createApplicationExistsException() {
        Application application = new EasyRandom().nextObject(Application.class);
        ApplicationDTO applicationDTO = ApplicationDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(application)).when(applicationRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> applicationService.createApplication(applicationDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createApplicationException() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(applicationRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(applicationRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> applicationService.createApplication(applicationDTO));
        assertEquals(MessageUtils.APPLICATION_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateApplication() throws ApplicationNotFoundException {
        Application application = new EasyRandom().nextObject(Application.class);
        ApplicationDTO applicationDTO = ApplicationDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(application)).when(applicationRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = applicationService.updateApplication(applicationDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateApplicationException(){
        Application application = new EasyRandom().nextObject(Application.class);
        ApplicationDTO applicationDTO = ApplicationDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(application)).when(applicationRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(applicationRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> applicationService.updateApplication(applicationDTO));
        assertEquals(MessageUtils.APPLICATION_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void applicationReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Application application = Application.builder().id("1").description("Teste").build();
        List<Application> applications = List.of(application, application);
        Pageable paging = PageRequest.of(0, 10);
        Page<Application> pagedResult = new PageImpl<>(applications, paging, applications.size());
        Mockito.doReturn(pagedResult).when(applicationRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = applicationService.applicationReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void applicationReportEmpty(){
        List<Application> applications = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Application> pagedResult = new PageImpl<>(applications, paging, applications.size());
        Mockito.doReturn(pagedResult).when(applicationRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> applicationService.applicationReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws ApplicationNotFoundException {
        Application application = new EasyRandom().nextObject(Application.class);
        Mockito.doReturn(Optional.of(application)).when(applicationRepository).findById(Mockito.any());
        applicationService.deleteById(application.getId());
        Mockito.verify(applicationRepository, Mockito.times(1)).deleteById(application.getId());
    }

    @Test
    void deleteByIdException()  {
        Application application = new EasyRandom().nextObject(Application.class);
        Mockito.doReturn(Optional.of(application)).when(applicationRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(applicationRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> applicationService.deleteById("1"));
        assertEquals(MessageUtils.APPLICATION_DELETE_ERROR.getDescription(), e.getMessage());
    }
}