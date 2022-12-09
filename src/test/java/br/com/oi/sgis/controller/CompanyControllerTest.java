package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.CompanyNotFoundException;
import br.com.oi.sgis.service.CompanyService;
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
class CompanyControllerTest {

    @InjectMocks
    private CompanyController companyController;

    @Mock
    private CompanyService companyService;

    @Test
    void listAllPaginated(){
        List<CompanyDTO> companiesDTOS = new EasyRandom().objects(CompanyDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(companiesDTOS, paging, companiesDTOS.size()));

        Mockito.doReturn(expectedResponse).when(companyService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<CompanyDTO>> response = companyController.listAllPaginated(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void listAllCompanyWithoutParameterPaginated(){
        List<CompanyDTO> companiesDTOS = new EasyRandom().objects(CompanyDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(companiesDTOS, paging, companiesDTOS.size()));

        Mockito.doReturn(expectedResponse).when(companyService).listAllCompanyWithoutParameterPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<CompanyDTO>> response = companyController.listAllCompanyWithoutParameterPaginated(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws CompanyNotFoundException {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        Mockito.doReturn(companyDTO).when(companyService).findById(Mockito.any());
        CompanyDTO companyDTOToReturn = companyController.findById("1L");

        assertEquals(companyDTO.getId(), companyDTOToReturn.getId());
    }

    @Test
    void createCompany() {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(companyService).createCompany(Mockito.any());
        MessageResponseDTO returnedResponse = companyController.createCompany(companyDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateCompany() throws CompanyNotFoundException {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(companyService).updateCompany(Mockito.any());
        MessageResponseDTO returnedResponse = companyController.updateCompany(companyDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(companyService).companyReport(Mockito.any(), Mockito.any(), Mockito.any());
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = companyController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void operatorsActive() {
        List<CompanyDTO> companiesDTOS = new EasyRandom().objects(CompanyDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("companyName"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(companiesDTOS, paging, companiesDTOS.size()));

        Mockito.doReturn(expectedResponse).when(companyService).listAllOperatorActives(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<CompanyDTO>> response = companyController.operatorsActive(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void transferProvider() throws CompanyNotFoundException {
        TransferProviderDTO transferProviderDTO = new EasyRandom().nextObject(TransferProviderDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(companyService).transferProvider(Mockito.any());
        MessageResponseDTO returnedResponse = companyController.transferProvider(transferProviderDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void instClientByProvider() throws CompanyNotFoundException {
        ClientInstByProviderDTO clientInstByProviderDTO = new EasyRandom().nextObject(ClientInstByProviderDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(companyService).instClientByProvider(Mockito.any());
        MessageResponseDTO returnedResponse = companyController.instClientByProvider(clientInstByProviderDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void listAllClients() {
        List<CompanyDTO> companiesDTOS = new EasyRandom().objects(CompanyDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("companyName"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(companiesDTOS, paging, companiesDTOS.size()));

        Mockito.doReturn(expectedResponse).when(companyService).listAllClients(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<CompanyDTO>> response = companyController.listAllClients(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void instClientByTechnician() throws CompanyNotFoundException {
        ClientInstByProviderDTO clientInstByProviderDTO = new EasyRandom().nextObject(ClientInstByProviderDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(companyService).instClientByTechnician(Mockito.any());
        MessageResponseDTO returnedResponse = companyController.instClientByTechnician(clientInstByProviderDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void emitProofProvider() throws JRException, CompanyNotFoundException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(companyService).emitProofProvider(Mockito.any());
        EmitProofProviderDTO emitProofDTO = new EasyRandom().nextObject(EmitProofProviderDTO.class);
        ResponseEntity<byte[]> responseReport = companyController.emitProofProvider(emitProofDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void listAllForUsersExtraction() {
        List<CompanyDTO> companiesDTOS = new EasyRandom().objects(CompanyDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("companyName"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(companiesDTOS, paging, companiesDTOS.size()));

        Mockito.doReturn(expectedResponse).when(companyService).listAllForUsersExtraction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<CompanyDTO>> response = companyController.listAllForUsersExtraction(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }
}