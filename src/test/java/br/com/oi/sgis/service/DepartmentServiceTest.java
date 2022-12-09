package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Parameter;
import br.com.oi.sgis.entity.TechnicalStaff;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.exception.TechnicalStaffNotFoundException;
import br.com.oi.sgis.mapper.DepartmentMapper;
import br.com.oi.sgis.repository.DepartmentRepository;
import br.com.oi.sgis.repository.ParameterRepository;
import br.com.oi.sgis.repository.TechnicalStaffRepository;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @InjectMocks
    private DepartmentService departmentService;
    @Mock
    private TechnicalStaffRepository technicalStaffRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private ParameterRepository parameterRepository;
    @Mock
    private ReportService reportService;

    @MockBean
    private DepartmentMapper departmentMapper = DepartmentMapper.INSTANCE;

    @Test
    void findById() throws DepartmentNotFoundException {
        Department department = new EasyRandom().nextObject(Department.class);
        Mockito.doReturn(Optional.of(department)).when(departmentRepository).findById(Mockito.any());
        DepartmentDTO departmentToReturn = departmentService.findById("1L");
        Assertions.assertEquals(department.getId(), departmentToReturn.getId());
    }

    @Test
    void shouldFindByIdWithException() {
        Mockito.doReturn(Optional.empty()).when(departmentRepository).findById(Mockito.any());
        Assertions.assertThrows(DepartmentNotFoundException.class, () -> departmentService.findById("1L"));
    }

    @Test
    void createDepartment(){
        Department department = Department.builder().id("1L").build();
        DepartmentDTO departmentDTO = departmentMapper.toDTO(department);
        Mockito.doReturn(department).when(departmentRepository).save(Mockito.any());
        Mockito.doReturn(Optional.of(Parameter.builder().build())).when(parameterRepository).findTopByAbbreviationIsContaining(Mockito.any());
        Mockito.doReturn(Optional.of(TechnicalStaff.builder().build())).when(technicalStaffRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = departmentService.createDepartment(departmentDTO);

        Assertions.assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
        Mockito.verify(departmentRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void createDepartmentException(){
        Department department = Department.builder().id("1L").build();
        DepartmentDTO departmentDTO = departmentMapper.toDTO(department);
        Mockito.doThrow(JpaSystemException.class).when(departmentRepository).save(Mockito.any());
        Mockito.doReturn(Optional.of(Parameter.builder().build())).when(parameterRepository).findTopByAbbreviationIsContaining(Mockito.any());
        Mockito.doReturn(Optional.of(TechnicalStaff.builder().build())).when(technicalStaffRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, ()->departmentService.createDepartment(departmentDTO));
        assertEquals(MessageUtils.DEPARTMENT_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void createDepartmentValidateParameterException(){
        Department department = Department.builder().id("1L").build();
        DepartmentDTO departmentDTO = departmentMapper.toDTO(department);
        Mockito.doReturn(Optional.empty()).when(parameterRepository).findTopByAbbreviationIsContaining(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, ()->departmentService.createDepartment(departmentDTO));
        assertEquals(MessageUtils.DEPARTMENT_SAVE_ERROR_PARAMETER.getDescription(), e.getMessage());
    }

    @Test
    void createDepartmentValidateTechException(){
        Department department = Department.builder().id("1L").manager(TechnicalStaff.builder().id("tech").build()).build();
        DepartmentDTO departmentDTO = departmentMapper.toDTO(department);
        Mockito.doReturn(Optional.empty()).when(technicalStaffRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(Parameter.builder().build())).when(parameterRepository).findTopByAbbreviationIsContaining(Mockito.any());

        Exception e = assertThrows(TechnicalStaffNotFoundException.class, ()->departmentService.createDepartment(departmentDTO));
        assertEquals(MessageUtils.TECHNICAL_STAFF_NOT_FOUND_BY_ID.getDescription() + "tech", e.getMessage());
    }

    @Test
    void createDepartmentValidateSisFlagException(){
        Department department = Department.builder().id("1L").systemFlag(true).responsibleCenter("Responsible").build();
        DepartmentDTO departmentDTO = departmentMapper.toDTO(department);

        Exception e = assertThrows(IllegalArgumentException.class, ()->departmentService.createDepartment(departmentDTO));
        assertEquals(MessageUtils.DEPARTMENT_SAVE_ERROR_RESPONSIBLE.getDescription() , e.getMessage());
    }

    @Test
    void createDepartmentValidateRepairCenterException(){
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);
        departmentDTO.setResponsibleCenter(null);
        departmentDTO.setRepairCenter(true);
        departmentDTO.setContractBudgetAnalysis(true);
        Exception e = assertThrows(IllegalArgumentException.class, ()->departmentService.createDepartment(departmentDTO));
        assertEquals(MessageUtils.DEPARTMENT_SAVE_ERROR_INFOS.getDescription() , e.getMessage());
    }


    @Test
    void listPaginated(){
        List<Department> departments = new EasyRandom().objects(Department.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Department> pagedResult = new PageImpl<>(departments, paging, departments.size());

        Mockito.doReturn(pagedResult).when(departmentRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<DepartmentDTO> departmentDTOSToReturn = departmentService.listPaginated(0, 10, List.of("id"), List.of("description"), "ABC");
        Assertions.assertEquals(departments.size(), departmentDTOSToReturn.getData().size());
    }

    @Test
    void shouldListAllWithSearchWithoutTerm(){
        List<Department> departments = new EasyRandom().objects(Department.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Department> pagedResult = new PageImpl<>(departments, paging, departments.size());

        Mockito.doReturn(pagedResult).when(departmentRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<DepartmentDTO> departmentDTOSToReturn = departmentService.listPaginated(0, 10, List.of("id"), List.of("description"), "");
        Assertions.assertEquals(departments.size(), departmentDTOSToReturn.getData().size());
    }

    @Test
    void updateDepartment() {
        Department department = Department.builder().id("1L").build();
        DepartmentDTO departmentDTO = departmentMapper.toDTO(department);
        Mockito.doReturn(department).when(departmentRepository).save(Mockito.any());
        Mockito.doReturn(Optional.of(department)).when(departmentRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(Parameter.builder().build())).when(parameterRepository).findTopByAbbreviationIsContaining(Mockito.any());
        Mockito.doReturn(Optional.of(TechnicalStaff.builder().build())).when(technicalStaffRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = departmentService.updateDepartment(departmentDTO);

        Assertions.assertEquals(HttpStatus.OK, responseDTO.getStatus());
        Mockito.verify(departmentRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void updateDepartmentException() {
        Department department = Department.builder().id("1L").build();
        DepartmentDTO departmentDTO = departmentMapper.toDTO(department);
        Mockito.doThrow(JpaSystemException.class).when(departmentRepository).save(Mockito.any());
        Mockito.doReturn(Optional.of(department)).when(departmentRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(Parameter.builder().build())).when(parameterRepository).findTopByAbbreviationIsContaining(Mockito.any());
        Mockito.doReturn(Optional.of(TechnicalStaff.builder().build())).when(technicalStaffRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, ()->departmentService.updateDepartment(departmentDTO));
        assertEquals(MessageUtils.DEPARTMENT_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void departmentReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Department department = Department.builder().id("1").description("Teste").build();
        List<Department> departments = List.of(department, department);
        Pageable paging = PageRequest.of(0, 10);
        Page<Department> pagedResult = new PageImpl<>(departments, paging, departments.size());
        Mockito.doReturn(pagedResult).when(departmentRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = departmentService.departmentReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void departmentReportEmpty(){
        List<Department> departments = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Department> pagedResult = new PageImpl<>(departments, paging, departments.size());
        Mockito.doReturn(pagedResult).when(departmentRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> departmentService.departmentReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void devolutionDepartmentByUnity() {
        Department department = new EasyRandom().nextObject(Department.class);
        Mockito.doReturn(Optional.of(department)).when(departmentRepository).findDevolutionDepMostRecent(Mockito.any());
        DepartmentDTO departmentDTO = departmentService.devolutionDepartmentByUnity("1450");
        assertEquals(department.getId(), departmentDTO.getId());
    }

    @Test
    void devolutionDepartmentByUnityNull() {
        Mockito.doReturn(Optional.empty()).when(departmentRepository).findDevolutionDepMostRecent(Mockito.any());
        DepartmentDTO departmentDTO = departmentService.devolutionDepartmentByUnity("1450");
        assertNull(departmentDTO);
    }

    @Test
    void listAllForUsersExtraction() {
        List<Department> departments = new EasyRandom().objects(Department.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Department> pagedResult = new PageImpl<>(departments, paging, departments.size());
        Mockito.doReturn(pagedResult).when(departmentRepository).findForUsersExtraction(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<DepartmentDTO> departmentDTOSToReturn = departmentService.listAllForUsersExtraction(0, 10, List.of("id"), List.of("description"), "ABC");
        Assertions.assertEquals(departments.size(), departmentDTOSToReturn.getData().size());
    }
}