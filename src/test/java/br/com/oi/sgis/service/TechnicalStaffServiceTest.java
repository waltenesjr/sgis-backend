package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.TechnicalStaff;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.TechnicalStaffNotFoundException;
import br.com.oi.sgis.mapper.TechnicalStaffMapper;
import br.com.oi.sgis.repository.DepartmentRepository;
import br.com.oi.sgis.repository.TechnicalStaffRepository;
import br.com.oi.sgis.repository.UnityRepository;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class TechnicalStaffServiceTest {

    @InjectMocks
    private TechnicalStaffService technicalStaffService;

    @Mock
    private TechnicalStaffRepository technicalStaffRepository;

    @MockBean
    private TechnicalStaffMapper technicalStaffMapper = TechnicalStaffMapper.INSTANCE;
    @Mock
    private ReportService reportService;
    @Mock
    private NasphService nasphService;
    @Mock
    private UnityRepository unityRepository;
    @Mock
    DepartmentRepository departmentRepository;

    @Test
    void findById() throws TechnicalStaffNotFoundException {
        TechnicalStaff technicalStaff = TechnicalStaff.builder().id("1L").build();

        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());
        TechnicalStaffDTO technicalStaffToReturn = technicalStaffService.findById("1L");

        Assertions.assertEquals(technicalStaff.getId(), technicalStaffToReturn.getId());
    }
    @Test
    void shouldFindByIdWithException() {
        Mockito.doReturn(Optional.empty()).when(technicalStaffRepository).findById(Mockito.any());

        Assertions.assertThrows(TechnicalStaffNotFoundException.class, () -> technicalStaffService.findById("1L"));
    }
    @Test
    void listPaginated(){
        List<TechnicalStaff> technicalStaffs = new EasyRandom().objects(TechnicalStaff.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<TechnicalStaff> pagedResult = new PageImpl<>(technicalStaffs, paging, technicalStaffs.size());

        Mockito.doReturn(pagedResult).when(technicalStaffRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<TechnicalStaffDTO> technicalStaffDTOSToReturn = technicalStaffService.listPaginated(0, 10, List.of("id"), List.of("description"), "S123");
        Assertions.assertEquals(technicalStaffs.size(), technicalStaffDTOSToReturn.getData().size());
    }

    @Test
    void shouldListAllWithSearchWithoutTerm(){
        List<TechnicalStaff> technicalStaffs = new EasyRandom().objects(TechnicalStaff.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<TechnicalStaff> pagedResult = new PageImpl<>(technicalStaffs, paging, technicalStaffs.size());

        Mockito.doReturn(pagedResult).when(technicalStaffRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<TechnicalStaffDTO> technicalStaffDTOSToReturn = technicalStaffService.listPaginated(0, 10, List.of("id"), List.of("description"), "");
        Assertions.assertEquals(technicalStaffs.size(), technicalStaffDTOSToReturn.getData().size());
    }

    @Test
    void createTechnicalStaff() {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        Mockito.doReturn(Optional.empty()).when(technicalStaffRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = technicalStaffService.createTechnicalStaff(technicalStaffDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createTechnicalStaffExistsException() {
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> technicalStaffService.createTechnicalStaff(technicalStaffDTO));
        assertEquals(MessageUtils.TECHNICAL_STAFF_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createTechnicalStaffException() {
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        Mockito.doReturn(Optional.empty()).when(technicalStaffRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(technicalStaffRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> technicalStaffService.createTechnicalStaff(technicalStaffDTO));
        assertEquals(MessageUtils.TECHNICAL_STAFF_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateTechnicalStaff() throws TechnicalStaffNotFoundException {
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = technicalStaffService.updateTechnicalStaff(technicalStaffDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateTechnicalStaffException(){
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(technicalStaffRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> technicalStaffService.updateTechnicalStaff(technicalStaffDTO));
        assertEquals(MessageUtils.TECHNICAL_STAFF_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void technicalStaffReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReportThreeColumns(Mockito.any(), Mockito.any());
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        List<TechnicalStaff> technicalStaffs = List.of(technicalStaff, technicalStaff);
        Pageable paging = PageRequest.of(0, 10);
        Page<TechnicalStaff> pagedResult = new PageImpl<>(technicalStaffs, paging, technicalStaffs.size());
        Mockito.doReturn(pagedResult).when(technicalStaffRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = technicalStaffService.technicalStaffReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void technicalStaffReportEmpty(){
        List<TechnicalStaff> technicalStaffs = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<TechnicalStaff> pagedResult = new PageImpl<>(technicalStaffs, paging, technicalStaffs.size());
        Mockito.doReturn(pagedResult).when(technicalStaffRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> technicalStaffService.technicalStaffReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws TechnicalStaffNotFoundException {
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());
        technicalStaffService.deleteById(technicalStaff.getId());
        Mockito.verify(technicalStaffRepository, Mockito.times(1)).deleteById(technicalStaff.getId());
    }

    @Test
    void deleteByIdException()  {
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(technicalStaffRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> technicalStaffService.deleteById("1"));
        assertEquals(MessageUtils.TECHNICAL_STAFF_DELETE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateManHour() throws TechnicalStaffNotFoundException {
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = technicalStaffService.updateManHour(technicalStaffDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void updateManHourException(){
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        TechnicalStaffDTO technicalStaffDTO = new EasyRandom().nextObject(TechnicalStaffDTO.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(technicalStaffRepository).updateManHour(Mockito.any(), Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> technicalStaffService.updateManHour(technicalStaffDTO));
        assertEquals(MessageUtils.TECHNICAL_STAFF_UPDATE_MH_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void listAllToForwardTicket() {
        List<TechnicalStaff> technicalStaffs = new EasyRandom().objects(TechnicalStaff.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<TechnicalStaff> pagedResult = new PageImpl<>(technicalStaffs, paging, technicalStaffs.size());

        Mockito.doReturn(pagedResult).when(technicalStaffRepository).findToForwardTicket(Mockito.any(), Mockito.anyString(),Mockito.any(Pageable.class));
        PaginateResponseDTO<TechnicalStaffDTO> technicalStaffDTOSToReturn = technicalStaffService.listAllToForwardTicket(0, 10, List.of("id"), List.of("description"), "");
        Assertions.assertEquals(technicalStaffs.size(), technicalStaffDTOSToReturn.getData().size());
    }

    @Test
    void transferTechnicalStaff() throws TechnicalStaffNotFoundException {
        TransferTechnicalDTO transferTechnicalDTO = new EasyRandom().nextObject(TransferTechnicalDTO.class);
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        Unity unity = new EasyRandom().nextObject(Unity.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(messageResponseDTO).when(nasphService).transferTechnicalStaff(Mockito.any());
        MessageResponseDTO responseDTO = technicalStaffService.transferTechnicalStaff(transferTechnicalDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void emitProof() throws JRException, IOException, TechnicalStaffNotFoundException {
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        Department department = new EasyRandom().nextObject(Department.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(department)).when(departmentRepository).findById(Mockito.any());
        EmitProofReportDTO emitProofReportDTO = new EmitProofReportDTO("123", "123", "123", "1230", "123",
                "123", "123", LocalDateTime.now());
        EmitProofDTO emitProofDTO = new EasyRandom().nextObject(EmitProofDTO.class);
        Mockito.doReturn(List.of(emitProofReportDTO)).when(technicalStaffRepository).emitProof(Mockito.any());
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).emitProofReport(Mockito.any(), Mockito.any());

        byte[] returnedReport = technicalStaffService.emitProof(emitProofDTO);
        assertNotNull(returnedReport);
    }

    @Test
    void emitProofEmpty(){
        TechnicalStaff technicalStaff = new EasyRandom().nextObject(TechnicalStaff.class);
        Department department = new EasyRandom().nextObject(Department.class);
        Mockito.doReturn(Optional.of(technicalStaff)).when(technicalStaffRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(department)).when(departmentRepository).findById(Mockito.any());
        EmitProofDTO emitProofDTO = new EasyRandom().nextObject(EmitProofDTO.class);
        Mockito.doReturn(List.of()).when(technicalStaffRepository).emitProof(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> technicalStaffService.emitProof(emitProofDTO ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());

    }

    @Test
    void listAllByUnity() {
        List<TechnicalStaff> technicalStaffs = new EasyRandom().objects(TechnicalStaff.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<TechnicalStaff> pagedResult = new PageImpl<>(technicalStaffs, paging, technicalStaffs.size());

        Mockito.doReturn(pagedResult).when(technicalStaffRepository).findLikeByUnity(Mockito.any(), Mockito.anyString(),Mockito.any(Pageable.class));
        PaginateResponseDTO<TechnicalStaffDTO> technicalStaffDTOSToReturn = technicalStaffService.listAllByUnity(0, 10, List.of("id"), List.of("description"), "");
        Assertions.assertEquals(technicalStaffs.size(), technicalStaffDTOSToReturn.getData().size());
    }
}