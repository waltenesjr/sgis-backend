package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DepartmentUnityDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.DepartmentUnityNotFoundException;
import br.com.oi.sgis.service.DepartmentUnityService;
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
class DepartmentUnityControllerTest {
    @InjectMocks
    private DepartmentUnityController departmentUnityController;

    @Mock
    private DepartmentUnityService departmentUnityService;

    @Test
    void listAllWithSearch(){
        List<DepartmentUnityDTO> departmentUnityDTOS = new EasyRandom().objects(DepartmentUnityDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(departmentUnityDTOS, paging, departmentUnityDTOS.size()));

        Mockito.doReturn(expectedResponse).when(departmentUnityService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<DepartmentUnityDTO>> response = departmentUnityController.listAllPaginated(0, 10,  List.of("departmentId"), List.of("modelUnityId"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws DepartmentUnityNotFoundException {
        DepartmentUnityDTO departmentUnityDTO = new EasyRandom().nextObject(DepartmentUnityDTO.class);
        Mockito.doReturn(departmentUnityDTO).when(departmentUnityService).findByIdDTO(Mockito.any());
        DepartmentUnityDTO departmentUnityDTOToReturn = departmentUnityController.findById("1L", "11");

        assertEquals(departmentUnityDTO.getDepartment().getId(), departmentUnityDTOToReturn.getDepartment().getId());
    }

    @Test
    void createDepartmentUnity() {
        DepartmentUnityDTO departmentUnityDTO = new EasyRandom().nextObject(DepartmentUnityDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(departmentUnityService).createDepartmentUnity(Mockito.any());
        MessageResponseDTO returnedResponse = departmentUnityController.createDepartmentUnity(departmentUnityDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateDepartmentUnity() throws DepartmentUnityNotFoundException {
        DepartmentUnityDTO departmentUnityDTO = new EasyRandom().nextObject(DepartmentUnityDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(departmentUnityService).updateDepartmentUnity(Mockito.any());
        MessageResponseDTO returnedResponse = departmentUnityController.updateDepartmentUnity(departmentUnityDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(departmentUnityService).departmentUnityReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = departmentUnityController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws DepartmentUnityNotFoundException {
        departmentUnityController.deleteById("1", "1");
        Mockito.verify(departmentUnityService, Mockito.times(1)).deleteById(Mockito.any());
    }

    @Test
    void createDepartmentUnityAdmin() {
        DepartmentUnityDTO departmentUnityDTO = new EasyRandom().nextObject(DepartmentUnityDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(departmentUnityService).createDepartmentUnityAdmin(Mockito.any());
        MessageResponseDTO returnedResponse = departmentUnityController.createDepartmentUnityAdmin(departmentUnityDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateDepartmentUnityAdmin() throws DepartmentUnityNotFoundException {
        DepartmentUnityDTO departmentUnityDTO = new EasyRandom().nextObject(DepartmentUnityDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(departmentUnityService).updateDepartmentUnityAdmin(Mockito.any());
        MessageResponseDTO returnedResponse = departmentUnityController.updateDepartmentUnityAdmin(departmentUnityDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateUnityLocation() throws DepartmentUnityNotFoundException {
        DepartmentUnityDTO departmentUnityDTO = new EasyRandom().nextObject(DepartmentUnityDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(departmentUnityService).updateLocationUnities(Mockito.any());
        MessageResponseDTO returnedResponse = departmentUnityController.updateUnityLocation(departmentUnityDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());

    }
}