package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.ContractDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.ContractNotFoundException;
import br.com.oi.sgis.service.ContractService;
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
class ContractControllerTest {

    @InjectMocks
    private ContractController contractController;

    @Mock
    private ContractService contractService;

    @Test
    void listAll() {
        List<ContractDTO> contractDTOS = new EasyRandom().objects(ContractDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(contractDTOS, paging, contractDTOS.size()));

        Mockito.doReturn(expectedResponse).when(contractService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ContractDTO>> response = contractController.listAll(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }


    @Test
    void findById() throws ContractNotFoundException {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Mockito.doReturn(contractDTO).when(contractService).findById(Mockito.any());
        ContractDTO contractDTOToReturn = contractController.findById("1L");

        assertEquals(contractDTO.getId(), contractDTOToReturn.getId());
    }

    @Test
    void createContract() {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(contractService).createContract(Mockito.any());
        MessageResponseDTO returnedResponse = contractController.createContract(contractDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateContract() throws ContractNotFoundException {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(contractService).updateContract(Mockito.any());
        MessageResponseDTO returnedResponse = contractController.updateContract(contractDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(contractService).contractReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = contractController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws ContractNotFoundException {
        contractController.deleteById("1");
        Mockito.verify(contractService, Mockito.times(1)).deleteById("1");
    }

    @Test
    void listForwardRepair() {
        List<ContractDTO> contractDTOS = new EasyRandom().objects(ContractDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(contractDTOS, paging, contractDTOS.size()));

        Mockito.doReturn(expectedResponse).when(contractService).listForwardRepair(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<ContractDTO>> response = contractController.listForwardRepair(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }
}