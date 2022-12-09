package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.DepartmentUnity;
import br.com.oi.sgis.exception.DepartmentUnityNotFoundException;
import br.com.oi.sgis.repository.DepartmentUnityRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
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
class DepartmentUnityServiceTest {
    @InjectMocks
    private DepartmentUnityService departmentUnityService;

    @Mock
    private DepartmentUnityRepository departmentUnityRepository;
    @Mock
    private ReportService reportService;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private AreaEquipamentService areaEquipamentService;
    private DepartmentUnityDTO departmentUnityDTO;
    private DepartmentUnity departmentUnity;
    @BeforeEach
    void setUp(){
        departmentUnityDTO = new EasyRandom().nextObject(DepartmentUnityDTO.class);
        departmentUnity = new EasyRandom().nextObject(DepartmentUnity.class);
        departmentUnityDTO.setDepartment(Utils.getUser().getDepartmentCode());

    }

    @Test
    void listAllPaginated(){
        List<DepartmentUnity> departmentUnitys = new EasyRandom().objects(DepartmentUnity.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<DepartmentUnity> pagedResult = new PageImpl<>(departmentUnitys, paging, departmentUnitys.size());

        Mockito.doReturn(pagedResult).when(departmentUnityRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<DepartmentUnityDTO> departmentUnitysToReturn = departmentUnityService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(departmentUnitys.size(), departmentUnitysToReturn.getData().size());
    }

    @Test
    void shouldListAllDepartmentUnitysWithoutTerm(){
        List<DepartmentUnity> departmentUnitys = new EasyRandom().objects(DepartmentUnity.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<DepartmentUnity> pagedResult = new PageImpl<>(departmentUnitys, paging, departmentUnitys.size());

        Mockito.doReturn(pagedResult).when(departmentUnityRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<DepartmentUnityDTO> departmentUnitysToReturn = departmentUnityService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(departmentUnitys.size(), departmentUnitysToReturn.getData().size());
    }

    @Test
    void findById() throws DepartmentUnityNotFoundException {
        Mockito.doReturn(Optional.of(departmentUnity)).when(departmentUnityRepository).findById(Mockito.any());

        DepartmentUnityDTO returndeDepartmentUnity = departmentUnityService.findByIdDTO(departmentUnityDTO);

        assertEquals(departmentUnity.getId().getDepartment().getId(), returndeDepartmentUnity.getDepartment().getId());
    }

    @Test
    void shouldDoThrowOnFindById(){

        Mockito.doReturn(Optional.empty()).when(departmentUnityRepository).findById(Mockito.any());
        Assertions.assertThrows(DepartmentUnityNotFoundException.class, () -> departmentUnityService.findByIdDTO(departmentUnityDTO));
    }

    @Test @SneakyThrows
    void createDepartmentUnity() {
        Mockito.doReturn(Optional.empty()).when(departmentUnityRepository).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(new AreaEquipamentDTO()).when(areaEquipamentService).findById(Mockito.any());
        MessageResponseDTO responseDTO = departmentUnityService.createDepartmentUnity(departmentUnityDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createDepartmentUnityExistsException() {
        Mockito.doReturn(Optional.of(departmentUnity)).when(departmentUnityRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> departmentUnityService.createDepartmentUnity(departmentUnityDTO));
        assertEquals(MessageUtils.DEP_UNITY_ALREADY_REGISTERED.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void createDepartmentUnityException() {
        Mockito.doReturn(Optional.empty()).when(departmentUnityRepository).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(new AreaEquipamentDTO()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(departmentUnityRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> departmentUnityService.createDepartmentUnity(departmentUnityDTO));
        assertEquals(MessageUtils.DEP_UNITY_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void differentDepartmentUserException() {
        departmentUnityDTO.setDepartment(DepartmentDTO.builder().id("Teste").build());
        Exception e = assertThrows(IllegalArgumentException.class, () -> departmentUnityService.createDepartmentUnity(departmentUnityDTO));
        assertEquals(MessageUtils.DEP_UNITY_DIFFERENT_USER_DEP.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void updateDepartmentUnity() {

        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(new AreaEquipamentDTO()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doReturn(Optional.of(departmentUnity)).when(departmentUnityRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = departmentUnityService.updateDepartmentUnity(departmentUnityDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test @SneakyThrows
    void updateDepartmentUnityException(){
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(new AreaEquipamentDTO()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doReturn(Optional.of(departmentUnity)).when(departmentUnityRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(departmentUnityRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> departmentUnityService.updateDepartmentUnity(departmentUnityDTO));
        assertEquals(MessageUtils.DEP_UNITY_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void departmentUnityReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).fillDepartmentUnityReport(Mockito.any(), Mockito.any());
        List<DepartmentUnity> departmentUnities = List.of(departmentUnity, departmentUnity);
        Pageable paging = PageRequest.of(0, 10);
        Page<DepartmentUnity> pagedResult = new PageImpl<>(departmentUnities, paging, departmentUnities.size());
        Mockito.doReturn(pagedResult).when(departmentUnityRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = departmentUnityService.departmentUnityReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void departmentUnityReportEmpty(){
        List<DepartmentUnity> departmentUnities = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<DepartmentUnity> pagedResult = new PageImpl<>(departmentUnities, paging, departmentUnities.size());
        Mockito.doReturn(pagedResult).when(departmentUnityRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> departmentUnityService.departmentUnityReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws DepartmentUnityNotFoundException {
        Mockito.doReturn(Optional.of(departmentUnity)).when(departmentUnityRepository).findById(Mockito.any());
        assertDoesNotThrow(()->departmentUnityService.deleteById(departmentUnityDTO));

    }

    @Test
    void deleteByIdException()  {
        Mockito.doReturn(Optional.of(departmentUnity)).when(departmentUnityRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(departmentUnityRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> departmentUnityService.deleteById(departmentUnityDTO));
        assertEquals(MessageUtils.DEP_UNITY_DELETE_ERROR.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void createDepartmentUnityAdmin() {
        departmentUnityDTO.setDepartment(DepartmentDTO.builder().id("A").build());
        Mockito.doReturn(Optional.empty()).when(departmentUnityRepository).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(new AreaEquipamentDTO()).when(areaEquipamentService).findById(Mockito.any());
        MessageResponseDTO responseDTO = departmentUnityService.createDepartmentUnityAdmin(departmentUnityDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test @SneakyThrows
    void updateLocationUnities() {
        departmentUnityDTO.setDepartment(DepartmentDTO.builder().id("A").build());
        Mockito.doReturn(Optional.of(departmentUnity)).when(departmentUnityRepository).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(new AreaEquipamentDTO()).when(areaEquipamentService).findById(Mockito.any());
        MessageResponseDTO responseDTO = departmentUnityService.updateLocationUnities(departmentUnityDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test @SneakyThrows
    void updateLocationUnitiesException() {
        departmentUnityDTO.setDepartment(DepartmentDTO.builder().id("A").build());
        Mockito.doReturn(Optional.of(departmentUnity)).when(departmentUnityRepository).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(new AreaEquipamentDTO()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(departmentUnityRepository).updateUnitiesByDepartmentUnity(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> departmentUnityService.updateLocationUnities(departmentUnityDTO));
        assertEquals(MessageUtils.DEP_UNITY_UPDATE_LOC_ERROR.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void updateDepartmentUnityAdmin() {
        departmentUnityDTO.setDepartment(DepartmentDTO.builder().id("A").build());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(new AreaEquipamentDTO()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doReturn(Optional.of(departmentUnity)).when(departmentUnityRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = departmentUnityService.updateDepartmentUnityAdmin(departmentUnityDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }
}