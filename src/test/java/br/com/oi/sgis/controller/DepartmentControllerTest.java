package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.service.DepartmentService;
import br.com.oi.sgis.util.PageableUtil;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    @InjectMocks
    private DepartmentController departmentController;

    @Mock
    private DepartmentService departmentService;

    @Test
    void findById() throws DepartmentNotFoundException {
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);
        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());
        DepartmentDTO departmentDTOToReturn = departmentController.findById("1L");

        assertEquals(departmentDTO.getId(), departmentDTOToReturn.getId());
    }

    @Test
    void shouldThrowExceptionWhenFindById() throws DepartmentNotFoundException {
        Mockito.doThrow(DepartmentNotFoundException.class).when(departmentService).findById(Mockito.any());

        Assertions.assertThrows(DepartmentNotFoundException.class, () -> departmentController.findById("1L"));
    }

    @Test
    void listAllPaginated(){
        List<DepartmentDTO> departmentDTOS = new EasyRandom().objects(DepartmentDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(departmentDTOS, paging, departmentDTOS.size()));

        Mockito.doReturn(expectedResponse).when(departmentService).listPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<DepartmentDTO>> response = departmentController.listAllPaginated(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void createDepartment() {
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(departmentService).createDepartment(Mockito.any());
        MessageResponseDTO returnedResponse = departmentController.createDepartment(departmentDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateDepartment() {
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(departmentService).updateDepartment(Mockito.any());
        MessageResponseDTO returnedResponse = departmentController.updateDepartment(departmentDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(departmentService).departmentReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = departmentController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void devolutionDepartment() {
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);
        Mockito.doReturn(departmentDTO).when(departmentService).devolutionDepartmentByUnity(Mockito.any());
        DepartmentDTO response = departmentController.devolutionDepartment("12");
        assertEquals(departmentDTO.getId(), response.getId());
    }

    @Test
    void listAllForUsersExtraction() {
        List<DepartmentDTO> departmentDTOS = new EasyRandom().objects(DepartmentDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(departmentDTOS, paging, departmentDTOS.size()));

        Mockito.doReturn(expectedResponse).when(departmentService).listAllForUsersExtraction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<DepartmentDTO>> response = departmentController.listAllForUsersExtraction(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }
}