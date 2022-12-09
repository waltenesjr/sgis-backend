package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.CurrencyTypeDTO;
import br.com.oi.sgis.dto.FiscalDocumentDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.FiscalDocument;
import br.com.oi.sgis.enums.CurrencyTypeEnum;
import br.com.oi.sgis.exception.FiscalDocumentNotFoundException;
import br.com.oi.sgis.service.FiscalDocumentService;
import br.com.oi.sgis.util.ExceptionHandlerAdvice;
import br.com.oi.sgis.util.PageableUtil;
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
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ExceptionHandlerAdvice.class)
class FiscalDocumentControllerTest {

    @InjectMocks
    private FiscalDocumentController fiscalDocumentController;

    @Mock
    private FiscalDocumentService fiscalDocumentService;

    @Test
    void listAllPaginated(){
        List<FiscalDocument> fiscalDocuments = new EasyRandom().objects(FiscalDocument.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(fiscalDocuments, paging, fiscalDocuments.size()));

        Mockito.doReturn(expectedResponse).when(fiscalDocumentService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.nullable(LocalDateTime.class),  Mockito.nullable(LocalDateTime.class));
        ResponseEntity<PaginateResponseDTO<Object>> response = fiscalDocumentController.listAllPaginated(0, 10, null, null,  List.of("id"), List.of("date"), "");

        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void shouldListAll(){
        List<FiscalDocument> fiscalDocuments = new EasyRandom().objects(FiscalDocument.class, 5).collect(Collectors.toList());

        Mockito.doReturn(fiscalDocuments).when(fiscalDocumentService).listAll();
        ResponseEntity<List<FiscalDocumentDTO>> response = fiscalDocumentController.listAll();

        Assertions.assertEquals(fiscalDocuments.size(), response.getBody().size());
        Assertions.assertNotNull(response.getBody().get(0));
    }

    @Test
    void findById() throws FiscalDocumentNotFoundException {
        FiscalDocumentDTO fiscalDocumentDTO = new EasyRandom().nextObject(FiscalDocumentDTO.class);
        Mockito.doReturn(fiscalDocumentDTO).when(fiscalDocumentService).findByIdDto(Mockito.any());
        FiscalDocumentDTO fiscalDocumentDTOToReturn = fiscalDocumentController.findById(fiscalDocumentDTO.getDocNumber(),fiscalDocumentDTO.getCompanyId(), fiscalDocumentDTO.getDocDate());

        assertEquals(fiscalDocumentDTO.getDocNumber(), fiscalDocumentDTOToReturn.getDocNumber());
    }

    @Test
    void createFiscalDocument() {
        FiscalDocumentDTO fiscalDocumentDTO = new EasyRandom().nextObject(FiscalDocumentDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(fiscalDocumentService).createDocument(Mockito.any());
        MessageResponseDTO returnedResponse = fiscalDocumentController.createFiscalDocument(fiscalDocumentDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateFiscalDocument() throws FiscalDocumentNotFoundException {
        FiscalDocumentDTO fiscalDocumentDTO = new EasyRandom().nextObject(FiscalDocumentDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(fiscalDocumentService).updateDocument(Mockito.any());
        MessageResponseDTO returnedResponse = fiscalDocumentController.updateFiscalDocument(fiscalDocumentDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void deleteById() throws FiscalDocumentNotFoundException {
        FiscalDocumentDTO fiscalDocumentDTO = new EasyRandom().nextObject(FiscalDocumentDTO.class);
        fiscalDocumentController.deleteById(fiscalDocumentDTO.getDocNumber(),fiscalDocumentDTO.getCompanyId(), fiscalDocumentDTO.getDocDate());
        assertDoesNotThrow(() ->         fiscalDocumentController.deleteById(fiscalDocumentDTO.getDocNumber(),fiscalDocumentDTO.getCompanyId(), fiscalDocumentDTO.getDocDate()));
    }

    @Test
    void currencyType() {
        List<CurrencyTypeDTO> currencyTypeDTOS = fiscalDocumentController.currencyType();
        assertEquals(CurrencyTypeEnum.values().length, currencyTypeDTOS.size());
    }
}