package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.FiscalDocument;
import br.com.oi.sgis.exception.FiscalDocumentNotFoundException;
import br.com.oi.sgis.mapper.FiscalDocumentMapper;
import br.com.oi.sgis.repository.FiscalDocumentRepository;
import br.com.oi.sgis.util.MessageUtils;
import lombok.SneakyThrows;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class FiscalDocumentServiceTest {
    @InjectMocks
    private FiscalDocumentService fiscalDocumentService;

    @Mock
    private FiscalDocumentRepository fiscalDocumentRepository;

    @MockBean
    private FiscalDocumentMapper fiscalDocumentMapper = FiscalDocumentMapper.INSTANCE;
    @Mock
    private ContractService contractService;
    @Mock
    private CompanyService companyService;
    private FiscalDocument fiscalDocument;
    private FiscalDocumentDTO fiscalDocumentDTO;
    @BeforeEach
    void setUp(){
        fiscalDocument = new EasyRandom().nextObject(FiscalDocument.class);
        fiscalDocumentDTO = new EasyRandom().nextObject(FiscalDocumentDTO.class);
        fiscalDocumentDTO.setCurrencyType(CurrencyTypeDTO.builder().id("RE").build());
    }

    @Test
    void listAll(){
        List<FiscalDocument> fiscalDocuments = new EasyRandom().objects(FiscalDocument.class, 5).collect(Collectors.toList());
        Mockito.doReturn(fiscalDocuments).when(fiscalDocumentRepository).findAll();

        List<FiscalDocumentDTO> fiscalDocumentsReturn = fiscalDocumentService.listAll();
        Assertions.assertEquals(fiscalDocuments.size(), fiscalDocumentsReturn.size());
    }

    @Test
    void listAllPaginated(){
        List<FiscalDocument> fiscalDocuments = new EasyRandom().objects(FiscalDocument.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<FiscalDocument> pagedResult = new PageImpl<>(fiscalDocuments, paging, fiscalDocuments.size());

        Mockito.doReturn(pagedResult).when(fiscalDocumentRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> fiscalDocumentsReturn = fiscalDocumentService.listAllPaginated(0, 10,List.of("id"), List.of("date"), "RJ-", null, null);
        Assertions.assertEquals(fiscalDocuments.size(), fiscalDocumentsReturn.getData().size());
    }

    @Test
    void shouldListAllWithSearchWithoutTerm(){
        List<FiscalDocument> fiscalDocuments = new EasyRandom().objects(FiscalDocument.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<FiscalDocument> pagedResult = new PageImpl<>(fiscalDocuments, paging, fiscalDocuments.size());

        Mockito.doReturn(pagedResult).when(fiscalDocumentRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> fiscalDocumentsReturn = fiscalDocumentService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "", null, null);
        Assertions.assertEquals(fiscalDocuments.size(), fiscalDocumentsReturn.getData().size());
    }

    @Test
    void listAllPaginatedByDate(){
        List<FiscalDocument> fiscalDocuments = new EasyRandom().objects(FiscalDocument.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<FiscalDocument> pagedResult = new PageImpl<>(fiscalDocuments, paging, fiscalDocuments.size());
        LocalDateTime finalDate = LocalDateTime.now();
        LocalDateTime initialDate = finalDate.minusDays(10);

        Mockito.doReturn(pagedResult).when(fiscalDocumentRepository).findLikeDateBetween(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> fiscalDocumentsReturn = fiscalDocumentService.listAllPaginated(0, 10,List.of("id"), List.of("date"), "RJ-", initialDate, finalDate);
        Assertions.assertEquals(fiscalDocuments.size(), fiscalDocumentsReturn.getData().size());
    }

    @Test
    void shouldListAllWithSearchWithoutTermAndDateBetween(){
        List<FiscalDocument> fiscalDocuments = new EasyRandom().objects(FiscalDocument.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<FiscalDocument> pagedResult = new PageImpl<>(fiscalDocuments, paging, fiscalDocuments.size());
        LocalDateTime finalDate = LocalDateTime.now();
        LocalDateTime initialDate = finalDate.minusDays(10);

        Mockito.doReturn(pagedResult).when(fiscalDocumentRepository).findAllByDateBetween(Mockito.any(), Mockito.any(),Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> fiscalDocumentsReturn = fiscalDocumentService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "", initialDate, finalDate);
        Assertions.assertEquals(fiscalDocuments.size(), fiscalDocumentsReturn.getData().size());
    }

    @Test
    void shouldThrowExceptionListAllWithDateBetween(){
        LocalDateTime finalDate = LocalDateTime.now();
        LocalDateTime initialDate = finalDate.plusDays(10);

        Exception e = Assertions.assertThrows(IllegalArgumentException.class, ()-> fiscalDocumentService.listAllPaginated(0, 10, null, null, "", initialDate, finalDate));
        Assertions.assertEquals("A data inicial não deve ser maior que a data final.", e.getMessage());
    }

    @Test
    void findById() throws FiscalDocumentNotFoundException {
        FiscalDocument fiscalDocument = new EasyRandom().nextObject(FiscalDocument.class);

        Mockito.doReturn(Optional.of(fiscalDocument)).when(fiscalDocumentRepository).findById(Mockito.any());
        FiscalDocumentDTO fiscalDocumentToReturn = fiscalDocumentService.findById(Mockito.any());

        Assertions.assertNotNull(fiscalDocumentToReturn);
        Assertions.assertEquals(fiscalDocument.getId().getCgcCPf().getId(), fiscalDocumentToReturn.getCompanyId());
    }
    @Test
    void shouldFindByIdWithException() {
        Mockito.doReturn(Optional.empty()).when(fiscalDocumentRepository).findById(Mockito.any());
        Assertions.assertThrows(FiscalDocumentNotFoundException.class, () -> fiscalDocumentService.findById(Mockito.any()));
    }

    @Test @SneakyThrows
    void findByIdDto() throws FiscalDocumentNotFoundException {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        Mockito.doReturn(companyDTO).when(companyService).findById(Mockito.any());
        Mockito.doReturn(Optional.of(fiscalDocument)).when(fiscalDocumentRepository).findById(Mockito.any());
        FiscalDocumentDTO fiscalDocumentToReturn = fiscalDocumentService.findByIdDto(fiscalDocumentDTO);
        Assertions.assertNotNull(fiscalDocumentToReturn);
        Assertions.assertEquals(fiscalDocument.getId().getCgcCPf().getId(), fiscalDocumentToReturn.getCompanyId());
    }

    @Test @SneakyThrows
    void createDocument() {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Mockito.doReturn(Optional.empty()).when(fiscalDocumentRepository).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(contractDTO).when(contractService).findById(Mockito.any());
        MessageResponseDTO responseDTO = fiscalDocumentService.createDocument(fiscalDocumentDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test @SneakyThrows
    void createDocumentExists() {
        Mockito.doReturn(Optional.of(fiscalDocument)).when(fiscalDocumentRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> fiscalDocumentService.createDocument(fiscalDocumentDTO));
        assertEquals(MessageUtils.DOCUMENT_ALREADY_REGISTERED.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void createDocumentWithException() {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Mockito.doReturn(Optional.empty()).when(fiscalDocumentRepository).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(contractDTO).when(contractService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(fiscalDocumentRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> fiscalDocumentService.createDocument(fiscalDocumentDTO));
        assertEquals(MessageUtils.DOCUMENT_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void updateDocument() {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Mockito.doReturn(Optional.of(fiscalDocument)).when(fiscalDocumentRepository).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(contractDTO).when(contractService).findById(Mockito.any());
        MessageResponseDTO responseDTO = fiscalDocumentService.updateDocument(fiscalDocumentDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test @SneakyThrows
    void updateDocumentWithException() {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Mockito.doReturn(Optional.of(fiscalDocument)).when(fiscalDocumentRepository).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(contractDTO).when(contractService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(fiscalDocumentRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> fiscalDocumentService.updateDocument(fiscalDocumentDTO));
        assertEquals(MessageUtils.DOCUMENT_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void deleteByDto() throws FiscalDocumentNotFoundException {
        Mockito.doReturn(Optional.of(fiscalDocument)).when(fiscalDocumentRepository).findById(Mockito.any());
        fiscalDocumentService.deleteByDto(fiscalDocumentDTO);
        Mockito.verify(fiscalDocumentRepository, Mockito.times(1)).deleteById(Mockito.any());
    }

    @Test
    void deleteByDtoException() {
        Mockito.doReturn(Optional.of(fiscalDocument)).when(fiscalDocumentRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(fiscalDocumentRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> fiscalDocumentService.deleteByDto(fiscalDocumentDTO));
        assertEquals(MessageUtils.DOCUMENT_DELETE_ERROR.getDescription(), e.getMessage());
    }
}