package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.ManufacturerDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.entity.Manufacturer;
import br.com.oi.sgis.exception.ManufacturerNotFoundException;
import br.com.oi.sgis.service.ManufacturerService;
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
class ManufacturerControllerTest {

    @InjectMocks
    private ManufacturerController manufacturerController;

    @Mock
    private ManufacturerService manufacturerService;

    @Test
    void listAllPaginated(){
        List<Manufacturer> manufacturers = new EasyRandom().objects(Manufacturer.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(manufacturers, paging, manufacturers.size()));

        Mockito.doReturn(expectedResponse).when(manufacturerService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ManufacturerDTO>> response = manufacturerController.listAllPaginated(0, 10,  List.of("id"), List.of(), "");

        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws ManufacturerNotFoundException {
        ManufacturerDTO manufacturerDTO = new EasyRandom().nextObject(ManufacturerDTO.class);
        Mockito.doReturn(manufacturerDTO).when(manufacturerService).findById(Mockito.any());
        ManufacturerDTO manufacturerDTOToReturn = manufacturerController.findById("1L");

        assertEquals(manufacturerDTO.getId(), manufacturerDTOToReturn.getId());
    }

    @Test
    void createManufacturer() {
        ManufacturerDTO manufacturerDTO = new EasyRandom().nextObject(ManufacturerDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(manufacturerService).createManufacturer(Mockito.any());
        MessageResponseDTO returnedResponse = manufacturerController.createManufacturer(manufacturerDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateManufacturer() throws ManufacturerNotFoundException {
        ManufacturerDTO manufacturerDTO = new EasyRandom().nextObject(ManufacturerDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(manufacturerService).updateManufacturer(Mockito.any());
        MessageResponseDTO returnedResponse = manufacturerController.updateManufacturer(manufacturerDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(manufacturerService).manufacturerReport(Mockito.any(), Mockito.any(), Mockito.any());
        ManufacturerDTO manufacturerDTO = new EasyRandom().nextObject(ManufacturerDTO.class);
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = manufacturerController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws ManufacturerNotFoundException {
        manufacturerController.deleteById("1");
        Mockito.verify(manufacturerService, Mockito.times(1)).deleteById("1");
    }

}