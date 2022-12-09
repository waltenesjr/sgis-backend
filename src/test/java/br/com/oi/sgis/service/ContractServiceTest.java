package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Contract;
import br.com.oi.sgis.exception.CompanyNotFoundException;
import br.com.oi.sgis.exception.ContractNotFoundException;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.repository.ContractRepository;
import br.com.oi.sgis.repository.ModelContractRepository;
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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {
    @InjectMocks
    private ContractService contractService;
    @Mock
    private ContractRepository contractRepository;
    @Mock
    private ReportService reportService;
    @Mock
    private CompanyService companyService;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private ModelContractRepository modelContractRepository;

    @Test
    void listAllPaginated() {
        List<Contract> contracts = new EasyRandom().objects(Contract.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Contract> pagedResult = new PageImpl<>(contracts, paging, contracts.size());

        Mockito.doReturn(pagedResult).when(contractRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<ContractDTO> contractDTOSToReturn = contractService.listAllPaginated(0, 10, List.of("id"), List.of("description"), "ABC");
        Assertions.assertEquals(contracts.size(), contractDTOSToReturn.getData().size());
    }

    @Test
    void listAllPaginatedWithoutTerm() {
        List<Contract> contracts = new EasyRandom().objects(Contract.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Contract> pagedResult = new PageImpl<>(contracts, paging, contracts.size());

        Mockito.doReturn(pagedResult).when(contractRepository).findAll( Mockito.any(Pageable.class));
        PaginateResponseDTO<ContractDTO> contractDTOSToReturn = contractService.listAllPaginated(0, 10, List.of("id"), List.of("description"), "");
        Assertions.assertEquals(contracts.size(), contractDTOSToReturn.getData().size());
    }

    @Test
    void findById() throws ContractNotFoundException {
        Contract contract = new EasyRandom().nextObject(Contract.class);
        Mockito.doReturn(Optional.of(contract)).when(contractRepository).findById(Mockito.any());
        ContractDTO contractDTO = contractService.findById("contractId");
        assertEquals(contract.getId(), contractDTO.getId());
    }
    @Test
    void findByIdException() {
        Mockito.doReturn(Optional.empty()).when(contractRepository).findById(Mockito.any());
        Exception e = assertThrows(ContractNotFoundException.class, () -> contractService.findById("contractId"));
        assertEquals(MessageUtils.CONTRACT_NOT_FOUND_BY_ID.getDescription() + "contractId", e.getMessage());
    }

    @Test
    void createContract() throws CompanyNotFoundException, DepartmentNotFoundException {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Mockito.doReturn(Optional.empty()).when(contractRepository).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        MessageResponseDTO responseDTO = contractService.createContract(contractDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createContractExistsException() {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Mockito.doReturn(Optional.of(new Contract())).when(contractRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> contractService.createContract(contractDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createContractException() throws CompanyNotFoundException, DepartmentNotFoundException {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Mockito.doReturn(Optional.empty()).when(contractRepository).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(contractRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> contractService.createContract(contractDTO));
        assertEquals(MessageUtils.CONTRACT_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void createContractExceptionModelContract() throws CompanyNotFoundException, DepartmentNotFoundException {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Mockito.doReturn(Optional.empty()).when(contractRepository).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(modelContractRepository).saveAll(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> contractService.createContract(contractDTO));
        assertEquals(MessageUtils.CONTRACT_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateContract() throws ContractNotFoundException, CompanyNotFoundException, DepartmentNotFoundException {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Contract contract = new EasyRandom().nextObject(Contract.class);
        Mockito.doReturn(Optional.of(contract)).when(contractRepository).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        MessageResponseDTO responseDTO = contractService.updateContract(contractDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
        Mockito.verify(modelContractRepository, Mockito.times(1)).saveAll(Mockito.any());
        Mockito.verify(modelContractRepository, Mockito.times(1)).deleteAll(Mockito.any());
    }
    @Test
    void updateContractIncludeModelContract() throws ContractNotFoundException, CompanyNotFoundException, DepartmentNotFoundException {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Contract contract = new EasyRandom().nextObject(Contract.class);
        contract.setModelContracts(List.of());
        Mockito.doReturn(Optional.of(contract)).when(contractRepository).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        MessageResponseDTO responseDTO = contractService.updateContract(contractDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
        Mockito.verify(modelContractRepository, Mockito.times(1)).saveAll(Mockito.any());
        Mockito.verify(modelContractRepository, Mockito.times(0)).deleteAll(Mockito.any());
    }

    @Test
    void updateContractRemoveAllModelContract() throws ContractNotFoundException, CompanyNotFoundException, DepartmentNotFoundException {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Contract contract = new EasyRandom().nextObject(Contract.class);
        contractDTO.setModelContracts(List.of());
        Mockito.doReturn(Optional.of(contract)).when(contractRepository).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        MessageResponseDTO responseDTO = contractService.updateContract(contractDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
        Mockito.verify(modelContractRepository, Mockito.times(0)).saveAll(Mockito.any());
        Mockito.verify(modelContractRepository, Mockito.times(1)).deleteAll(Mockito.any());
    }

    @Test
    void updateContractException() throws CompanyNotFoundException, DepartmentNotFoundException {
        ContractDTO contractDTO = new EasyRandom().nextObject(ContractDTO.class);
        Contract contract = new EasyRandom().nextObject(Contract.class);
        Mockito.doReturn(Optional.of(contract)).when(contractRepository).findById(Mockito.any());
        Mockito.doReturn(new CompanyDTO()).when(companyService).findById(Mockito.any());
        Mockito.doReturn(new DepartmentDTO()).when(departmentService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(contractRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> contractService.updateContract(contractDTO));
        assertEquals(MessageUtils.CONTRACT_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void contractReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).fillContractReport(Mockito.any(), Mockito.any());
        Contract contract = Contract.builder().id("1").description("Teste").build();
        List<Contract> contracts = List.of(contract, contract);
        Pageable paging = PageRequest.of(0, 10);
        Page<Contract> pagedResult = new PageImpl<>(contracts, paging, contracts.size());
        Mockito.doReturn(pagedResult).when(contractRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = contractService.contractReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void contractEmptyReport()  {
        List<Contract> contracts = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Contract> pagedResult = new PageImpl<>(contracts, paging, contracts.size());
        Mockito.doReturn(pagedResult).when(contractRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> contractService.contractReport("",null, null));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws ContractNotFoundException {
        Contract contract = new EasyRandom().nextObject(Contract.class);
        Mockito.doReturn(Optional.of(contract)).when(contractRepository).findById(Mockito.any());
        contractService.deleteById(contract.getId());
        Mockito.verify(contractRepository, Mockito.times(1)).deleteById(contract.getId());
    }

    @Test
    void deleteByIdException()  {
        Contract contract = new EasyRandom().nextObject(Contract.class);
        Mockito.doReturn(Optional.of(contract)).when(contractRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(contractRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> contractService.deleteById("1"));
        assertEquals(MessageUtils.CONTRACT_DELETE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void listForwardRepair() {
        List<Contract> contracts = new EasyRandom().objects(Contract.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Contract> pagedResult = new PageImpl<>(contracts, paging, contracts.size());

        Mockito.doReturn(pagedResult).when(contractRepository).listForwardRepair( Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<ContractDTO> contractDTOSToReturn = contractService.listForwardRepair(0, 10, List.of("id"), List.of("description"), "");
        Assertions.assertEquals(contracts.size(), contractDTOSToReturn.getData().size());
    }
}