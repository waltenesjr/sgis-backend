package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.CompanyModelDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.CompanyModelNotFoundException;
import br.com.oi.sgis.service.CompanyModelService;
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
class CompanyModelControllerTest {
    @InjectMocks
    private CompanyModelController companyModelController;

    @Mock
    private CompanyModelService companyModelService;

    @Test
    void listAllWithSearch(){
        List<CompanyModelDTO> companyModelDTOS = new EasyRandom().objects(CompanyModelDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(companyModelDTOS, paging, companyModelDTOS.size()));

        Mockito.doReturn(expectedResponse).when(companyModelService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<CompanyModelDTO>> response = companyModelController.listAllPaginated(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void findById() throws CompanyModelNotFoundException {
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);
        Mockito.doReturn(companyModelDTO).when(companyModelService).findByIdDTO(Mockito.any());
        CompanyModelDTO companyModelDTOToReturn = companyModelController.findById("compId","equipId", "depId");

        assertEquals(companyModelDTO.getCompany().getId(), companyModelDTOToReturn.getCompany().getId());
    }

    @Test
    void createCompanyModel() {
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(companyModelService).createCompanyModel(Mockito.any());
        MessageResponseDTO returnedResponse = companyModelController.createCompanyModel(companyModelDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateCompanyModel() throws CompanyModelNotFoundException {
        CompanyModelDTO companyModelDTO = new EasyRandom().nextObject(CompanyModelDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(companyModelService).updateCompanyModel(Mockito.any());
        MessageResponseDTO returnedResponse = companyModelController.updateCompanyModel(companyModelDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(companyModelService).companyModelReport(Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = companyModelController.report(reportCrudSearchDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void deleteById() throws CompanyModelNotFoundException {
        companyModelController.deleteById("compId","equipId", "depId");
        Mockito.verify(companyModelService, Mockito.times(1)).deleteById(Mockito.any());
    }
}
