package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Company;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.CompanyNotFoundException;
import br.com.oi.sgis.repository.CompanyRepository;
import br.com.oi.sgis.repository.DepartmentRepository;
import br.com.oi.sgis.repository.UnityRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private ReportService reportService;
    @Mock
    private ManufacturerService manufacturerService;
    @Mock
    private AddressService addressService;
    @Mock
    private NasphService nasphService;
    @Mock
    private UnityRepository unityRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    @Test
    void shouldListAllApplicationsWithTerm(){
        List<Company> companies = new EasyRandom().objects(Company.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Company> pagedResult = new PageImpl<>(companies, paging, companies.size());

        Mockito.doReturn(pagedResult).when(companyRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<CompanyDTO> companiesToReturn = companyService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        Assertions.assertEquals(companies.size(), companiesToReturn.getData().size());
    }

    @Test
    void shouldListAllCompanysWithoutTerm(){
        List<Company> companies = new EasyRandom().objects(Company.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Company> pagedResult = new PageImpl<>(companies, paging, companies.size());

        Mockito.doReturn(pagedResult).when(companyRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<CompanyDTO> companiesToReturn = companyService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        Assertions.assertEquals(companies.size(), companiesToReturn.getData().size());
    }

    @Test
    void shouldListAllCompaniesWithoutParameterWithTerm(){
        List<Company> companies = new EasyRandom().objects(Company.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Company> pagedResult = new PageImpl<>(companies, paging, companies.size());

        Mockito.doReturn(pagedResult).when(companyRepository).findAllCompanyWithoutParameter(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<CompanyDTO> companiesToReturn = companyService.listAllCompanyWithoutParameterPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        Assertions.assertEquals(companies.size(), companiesToReturn.getData().size());
    }

    @Test
    void shouldListAllCompaniesWithoutParameterWithoutTerm(){
        List<Company> companies = new EasyRandom().objects(Company.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Company> pagedResult = new PageImpl<>(companies, paging, companies.size());

        Mockito.doReturn(pagedResult).when(companyRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<CompanyDTO> companiesToReturn = companyService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        Assertions.assertEquals(companies.size(), companiesToReturn.getData().size());
    }

    @Test
    void shouldFindById() throws CompanyNotFoundException {
        Company company = new EasyRandom().nextObject(Company.class);
        Mockito.doReturn(Optional.of(company)).when(companyRepository).findById(Mockito.anyString());

        CompanyDTO companyDTO = companyService.findById(company.getId());

        Assertions.assertEquals(company.getId(), companyDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Company company = new EasyRandom().nextObject(Company.class);
        Mockito.doReturn(Optional.empty()).when(companyRepository).findById(Mockito.anyString());
        Assertions.assertThrows(CompanyNotFoundException.class, () -> companyService.findById(company.getId()));
    }

    @Test
    void createCompany()  {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        companyDTO.setCnpjCpf("94529144000183");
        companyDTO.setProvider(true); companyDTO.setManufacturer(false); companyDTO.setClient(false);
        Company lastCompany = new EasyRandom().nextObject(Company.class);;
        lastCompany.setId("00000000000001");
        Mockito.doReturn(Optional.empty()).when(companyRepository).findTopByCnpjCpfEquals(Mockito.any());
        Mockito.doReturn(lastCompany).when(companyRepository).findTopOrderByIdDesc();
        MessageResponseDTO responseDTO = companyService.createCompany(companyDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createCompanyWithAddress()  {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        companyDTO.setCnpjCpf("94529144000183");
        companyDTO.setProvider(true); companyDTO.setManufacturer(false); companyDTO.setClient(false);
        companyDTO.setAddresses(List.of());
        Company lastCompany = new EasyRandom().nextObject(Company.class);;
        lastCompany.setId("00000000000001");
        Mockito.doReturn(Optional.empty()).when(companyRepository).findTopByCnpjCpfEquals(Mockito.any());
        Mockito.doReturn(lastCompany).when(companyRepository).findTopOrderByIdDesc();
        Exception e = assertThrows(IllegalArgumentException.class, () -> companyService.createCompany(companyDTO));
        assertEquals(MessageUtils.COMPANY_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void createCompanyExistsException() {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        Mockito.doReturn(Optional.of(new Company())).when(companyRepository).findTopByCnpjCpfEquals(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> companyService.createCompany(companyDTO));
        assertEquals(MessageUtils.COMPANY_CNPJ_REGISTERER_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void createCompanyManufacturer()  {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        companyDTO.setCnpjCpf("94529144000183");
        companyDTO.setProvider(true); companyDTO.setManufacturer(true); companyDTO.setClient(false);
        Mockito.doReturn(Optional.empty()).when(companyRepository).findTopByCnpjCpfEquals(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> companyService.createCompany(companyDTO));
        assertEquals(MessageUtils.COMPANY_MANUFACTURER_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void createCompanyClientProviderOrHosting()  {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        companyDTO.setCnpjCpf("94529144000183");
        companyDTO.setProvider(true); companyDTO.setManufacturer(false); companyDTO.setClient(true);
        Mockito.doReturn(Optional.empty()).when(companyRepository).findTopByCnpjCpfEquals(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> companyService.createCompany(companyDTO));
        assertEquals(MessageUtils.COMPANY_PROVIDER_OR_HOLDING_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void createCompanyException() {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        companyDTO.setCnpjCpf("94529144000183");
        companyDTO.setProvider(true); companyDTO.setManufacturer(false); companyDTO.setClient(false);
        Company lastCompany = new EasyRandom().nextObject(Company.class);;
        lastCompany.setId("00000000000001");
        Mockito.doReturn(Optional.empty()).when(companyRepository).findTopByCnpjCpfEquals(Mockito.any());
        Mockito.doReturn(lastCompany).when(companyRepository).findTopOrderByIdDesc();
        Mockito.doThrow(SqlScriptParserException.class).when(companyRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> companyService.createCompany(companyDTO));
        assertEquals(MessageUtils.COMPANY_SAVE_ERROR.getDescription(), e.getMessage());
    }
    @Test
    void createCompanyValidateClient() {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        companyDTO.setCnpjCpf("45955732004");
        companyDTO.setProvider(false); companyDTO.setManufacturer(false); companyDTO.setClient(true);
        Company lastCompany = new EasyRandom().nextObject(Company.class);;
        lastCompany.setId("00000000000001");
        Mockito.doReturn(Optional.empty()).when(companyRepository).findTopByCnpjCpfEquals(Mockito.any());
        Mockito.doReturn(lastCompany).when(companyRepository).findTopOrderByIdDesc();
        Mockito.doThrow(SqlScriptParserException.class).when(companyRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> companyService.createCompany(companyDTO));
        assertEquals(MessageUtils.COMPANY_SAVE_ERROR.getDescription(), e.getMessage());
    }


    @Test
    void updateCompany() throws CompanyNotFoundException {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        companyDTO.setCnpjCpf("94529144000183");
        companyDTO.setProvider(true); companyDTO.setManufacturer(false); companyDTO.setClient(false);
        Company company = new EasyRandom().nextObject(Company.class);
        Mockito.doReturn(Optional.of(company)).when(companyRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = companyService.updateCompany(companyDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void updateCompanyException() {
        CompanyDTO companyDTO = new EasyRandom().nextObject(CompanyDTO.class);
        companyDTO.setCnpjCpf("94529144000183");
        companyDTO.setProvider(true); companyDTO.setManufacturer(false); companyDTO.setClient(false);
        Company company = new EasyRandom().nextObject(Company.class);
        Mockito.doReturn(Optional.of(company)).when(companyRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(companyRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> companyService.updateCompany(companyDTO));
        assertEquals(MessageUtils.COMPANY_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void companyReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Company company = Company.builder().id("1").companyName("Teste").build();
        List<Company> companys = List.of(company, company);
        Pageable paging = PageRequest.of(0, 10);
        Page<Company> pagedResult = new PageImpl<>(companys, paging, companys.size());
        Mockito.doReturn(pagedResult).when(companyRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = companyService.companyReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void companyReportEmpty(){
        List<Company> companies = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Company> pagedResult = new PageImpl<>(companies, paging, 0);
        Mockito.doReturn(pagedResult).when(companyRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> companyService.companyReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }


    @Test
    void listAllOperatorActives() {
        List<Company> companies = new EasyRandom().objects(Company.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Company> pagedResult = new PageImpl<>(companies, paging, companies.size());

        Mockito.doReturn(pagedResult).when(companyRepository).findAllByActiveTrueAndHoldingTrue(Mockito.any(),Mockito.any(Pageable.class));
        PaginateResponseDTO<CompanyDTO> companiesToReturn = companyService.listAllOperatorActives(0, 10, List.of("id"), List.of("date"), "");
        Assertions.assertEquals(companies.size(), companiesToReturn.getData().size());
    }

    @Test
    void transferProvider() throws CompanyNotFoundException {
        TransferProviderDTO transferProviderDTO = new EasyRandom().nextObject(TransferProviderDTO.class);
        Company company = new EasyRandom().nextObject(Company.class);
        Mockito.doReturn(Optional.of(company)).when(companyRepository).findById(Mockito.any());
        Unity unity = new EasyRandom().nextObject(Unity.class);
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(messageResponseDTO).when(nasphService).transferProvider(Mockito.any());
        MessageResponseDTO responseDTO = companyService.transferProvider(transferProviderDTO);
        assertEquals(messageResponseDTO.getStatus(), responseDTO.getStatus());
    }

    @Test
    void instClientByProvider() throws CompanyNotFoundException {
        ClientInstByProviderDTO clientInstByProviderDTO = new EasyRandom().nextObject(ClientInstByProviderDTO.class);
        Company company = new EasyRandom().nextObject(Company.class);
        clientInstByProviderDTO.setAddressId(company.getAddresses().get(0).getId());
        Mockito.doReturn(Optional.of(company)).when(companyRepository).findById(Mockito.any());
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(messageResponseDTO).when(nasphService).instClientByProvider(Mockito.any());
        MessageResponseDTO responseDTO = companyService.instClientByProvider(clientInstByProviderDTO);
        assertEquals(messageResponseDTO.getStatus(), responseDTO.getStatus());
    }

    @Test
    void instClientByProviderAddressError()  {
        ClientInstByProviderDTO clientInstByProviderDTO = new EasyRandom().nextObject(ClientInstByProviderDTO.class);
        Company company = new EasyRandom().nextObject(Company.class);
        Mockito.doReturn(Optional.of(company)).when(companyRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> companyService.instClientByProvider(clientInstByProviderDTO));
        assertEquals(MessageUtils.INSTA_CLIENT_PROVIDER_ADDRESS_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void listAllClients() {
        List<Company> companies = new EasyRandom().objects(Company.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Company> pagedResult = new PageImpl<>(companies, paging, companies.size());

        Mockito.doReturn(pagedResult).when(companyRepository).findAllClient(Mockito.any(),Mockito.any(Pageable.class));
        PaginateResponseDTO<CompanyDTO> companiesToReturn = companyService.listAllClients(0, 10, List.of("id"), List.of("date"), "");
        Assertions.assertEquals(companies.size(), companiesToReturn.getData().size());
    }

    @Test
    void instClientByTechnician() throws CompanyNotFoundException {
        ClientInstByProviderDTO clientInstByProviderDTO = new EasyRandom().nextObject(ClientInstByProviderDTO.class);
        Company company = new EasyRandom().nextObject(Company.class);
        clientInstByProviderDTO.setAddressId(company.getAddresses().get(0).getId());
        Mockito.doReturn(Optional.of(company)).when(companyRepository).findById(Mockito.any());
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(messageResponseDTO).when(nasphService).instClientByTechnician(Mockito.any(), Mockito.any(), Mockito.any());
        MessageResponseDTO responseDTO = companyService.instClientByTechnician(clientInstByProviderDTO);
        assertEquals(messageResponseDTO.getStatus(), responseDTO.getStatus());
    }

    @Test
    void instClientByTechnicianError()  {
        ClientInstByProviderDTO clientInstByProviderDTO = new EasyRandom().nextObject(ClientInstByProviderDTO.class);
        Company company = new EasyRandom().nextObject(Company.class);
        Mockito.doReturn(Optional.of(company)).when(companyRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> companyService.instClientByTechnician(clientInstByProviderDTO));
        assertEquals(MessageUtils.INSTA_CLIENT_PROVIDER_ADDRESS_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void emitProofProvider() throws JRException, IOException, CompanyNotFoundException {
        Company company = new EasyRandom().nextObject(Company.class);
        Department department = new EasyRandom().nextObject(Department.class);
        Mockito.doReturn(Optional.of(company)).when(companyRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(department)).when(departmentRepository).findById(Mockito.any());
        EmitProofReportDTO emitProofReportDTO = new EmitProofReportDTO("123", "123", "123", "1230", "123",
                "123", "123", LocalDateTime.now());
        EmitProofProviderDTO emitProofDTO = new EasyRandom().nextObject(EmitProofProviderDTO.class);
        Mockito.doReturn(List.of(emitProofReportDTO)).when(companyRepository).emitProofProvider(Mockito.any());
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).emitProofProviderReport(Mockito.any(), Mockito.any());

        byte[] returnedReport = companyService.emitProofProvider(emitProofDTO);
        assertNotNull(returnedReport);
    }

    @Test
    void emitProofEmpty(){
        Company company = new EasyRandom().nextObject(Company.class);
        Department department = new EasyRandom().nextObject(Department.class);
        Mockito.doReturn(Optional.of(company)).when(companyRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.of(department)).when(departmentRepository).findById(Mockito.any());
        EmitProofProviderDTO emitProofDTO = new EasyRandom().nextObject(EmitProofProviderDTO.class);
        Mockito.doReturn(List.of()).when(companyRepository).emitProofProvider(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> companyService.emitProofProvider(emitProofDTO ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());

    }

    @Test
    void listAllForUsersExtraction() {
        List<Company> companies = new EasyRandom().objects(Company.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Company> pagedResult = new PageImpl<>(companies, paging, companies.size());

        Mockito.doReturn(pagedResult).when(companyRepository).findForUsersExtraction(Mockito.any(),Mockito.any(Pageable.class));
        PaginateResponseDTO<CompanyDTO> companiesToReturn = companyService.listAllForUsersExtraction(0, 10, List.of("id"), List.of("date"), "");
        Assertions.assertEquals(companies.size(), companiesToReturn.getData().size());
    }
}