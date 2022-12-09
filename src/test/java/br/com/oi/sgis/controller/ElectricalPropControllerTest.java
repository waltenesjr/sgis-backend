package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.EletricPropNotFoundException;
import br.com.oi.sgis.service.ElectricalPropService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ElectricalPropControllerTest {

    @Mock
    private ElectricalPropService electricalPropService;
    @InjectMocks
    private ElectricalPropController electricalPropController;

    @Test
    void listAll(){
        List<ElectricalPropDTO> propertyList = List.of(new EasyRandom().nextObject(ElectricalPropDTO.class));
        Mockito.doReturn(propertyList).when(electricalPropService).listAll();
        List<ElectricalPropDTO> propertiesReturned = electricalPropController.listAll();

        assertNotNull(propertiesReturned);
        assertEquals(propertyList.size(), propertiesReturned.size());
    }


    @Test
    void listAllWithSearch() {
        List<ElectricalPropDTO> electricalPropDTOS = new EasyRandom().objects(ElectricalPropDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<ElectricalPropDTO> expectedResponse = PageableUtil.paginate(new PageImpl(electricalPropDTOS, paging, electricalPropDTOS.size()));

        Mockito.doReturn(expectedResponse).when(electricalPropService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ElectricalPropDTO>> response = electricalPropController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws EletricPropNotFoundException {
        ElectricalPropDTO electricalPropDTO = new EasyRandom().nextObject(ElectricalPropDTO.class);
        Mockito.doReturn(electricalPropDTO).when(electricalPropService).findById(Mockito.any());
        ElectricalPropDTO electricalPropDTOToReturn = electricalPropController.findById("1L");

        assertEquals(electricalPropDTO.getId(), electricalPropDTOToReturn.getId());
    }

    @Test
    void createElectricalProp() {
        ElectricalPropDTO electricalPropDTO = new EasyRandom().nextObject(ElectricalPropDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(electricalPropService).createElectricalProperty(Mockito.any());
        MessageResponseDTO returnedResponse = electricalPropController.createElectricalProp(electricalPropDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateElectricalProp() throws EletricPropNotFoundException {
        ElectricalPropDTO electricalPropDTO = new EasyRandom().nextObject(ElectricalPropDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(electricalPropService).updateElectricalProperty(Mockito.any());
        MessageResponseDTO returnedResponse = electricalPropController.updateElectricalProp(electricalPropDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(electricalPropService).electricalPropReport(Mockito.any(), Mockito.any(), Mockito.any());
        ElectricalPropDTO electricalPropDTO = new EasyRandom().nextObject(ElectricalPropDTO.class);
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = electricalPropController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws EletricPropNotFoundException {
        electricalPropController.deleteById("1");
        Mockito.verify(electricalPropService, Mockito.times(1)).deleteById("1");
    }

    @Test
    void physicalElectricalProperty() {
        ElectricalPropFilterDTO filterDto = new EasyRandom().nextObject(ElectricalPropFilterDTO.class);
        List<PhysicalElectricalPropsDTO> electricalPropDTOS = new EasyRandom().objects(PhysicalElectricalPropsDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<ElectricalPropDTO> expectedResponse = PageableUtil.paginate(new PageImpl(electricalPropDTOS, paging, electricalPropDTOS.size()));

        Mockito.doReturn(expectedResponse).when(electricalPropService).physicalElectricalProperty(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<PhysicalElectricalPropsDTO>> response = electricalPropController.physicalElectricalProperty(0, 10,  List.of("id"), List.of("description"), filterDto);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void physicalElectricalPropertyReport() throws JRException, IOException {
        ElectricalPropFilterDTO filterDto = new EasyRandom().nextObject(ElectricalPropFilterDTO.class);
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(electricalPropService).physicalElectricalPropertyReport(Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<byte[]> responseReport = electricalPropController.physicalElectricalPropertyReport(List.of(), List.of(), filterDto);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }
}