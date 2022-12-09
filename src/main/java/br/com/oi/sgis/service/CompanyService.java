package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Company;
import br.com.oi.sgis.exception.CompanyNotFoundException;
import br.com.oi.sgis.mapper.CompanyMapper;
import br.com.oi.sgis.repository.CompanyRepository;
import br.com.oi.sgis.repository.DepartmentRepository;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CompanyService {

    private final CompanyRepository companyRepository;
    private static final CompanyMapper companyMapper = CompanyMapper.INSTANCE;
    private final ReportService reportService;
    private final ManufacturerService manufacturerService;
    private final AddressService addressService;
    private final UnityRepository unityRepository;
    private final NasphService nasphService;
    private final DepartmentRepository departmentRepository;

    public PaginateResponseDTO<CompanyDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(companyRepository.findAll(paging).map(companyMapper::toDTO));

        return PageableUtil.paginate(companyRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(companyMapper::toDTO));
    }

    public PaginateResponseDTO<CompanyDTO> listAllCompanyWithoutParameterPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            term = "";

        return PageableUtil.paginate(companyRepository.findAllCompanyWithoutParameter(term.toUpperCase(Locale.ROOT).trim(), paging).map(companyMapper::toDTO));
    }

    public PaginateResponseDTO<CompanyDTO> listAllOperatorActives(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            term = "";

        return PageableUtil.paginate(companyRepository.findAllByActiveTrueAndHoldingTrue(term.toUpperCase(Locale.ROOT).trim(), paging).map(companyMapper::toDTO));
    }

    public CompanyDTO findById(String id) throws CompanyNotFoundException {
        Company company = verifyIfExists(id);
        return companyMapper.toDTO(company);
    }

    private Company verifyIfExists(String id) throws CompanyNotFoundException {
        return companyRepository.findById(id)
                .orElseThrow(()-> new CompanyNotFoundException(MessageUtils.COMPANY_NOT_FOUND_BY_ID.getDescription() + id));
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO createCompany(CompanyDTO companyDTO) {
        Company company = companyMapper.toModel(companyDTO);
        validateExists(company);
        validateCompany(company);
        String id = verifyNewCompanyId();
        company.setId(id);
        try {
            Company savedCompany = companyRepository.save(company);
            saveAddresses(companyDTO.getAddresses(), companyMapper.toDTO(savedCompany));
            return createMessageResponse(company.getId(), MessageUtils.COMPANY_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPANY_SAVE_ERROR.getDescription());
        }
    }

    private void saveAddresses(List<AddressDTO> addresses, CompanyDTO savedCompany) {
        if(addresses ==null || addresses.isEmpty())
            throw new IllegalArgumentException(MessageUtils.COMPANY_ADDRESS_ERROR.getDescription());
        try {
            addresses.forEach(a -> {
                a.setCgcCpf(savedCompany);
                addressService.createAddress(a);
            });
        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.ADDRESS_SAVE_ERROR.getDescription());
        }
    }
    private void updateAddressess(CompanyDTO company) throws CompanyNotFoundException {
        List<AddressDTO> addresses = company.getAddresses();
        List<AddressDTO> oldAddresss = findById(company.getId()).getAddresses();
        if(addresses.isEmpty() && oldAddresss.isEmpty())
            return;
        if(oldAddresss.isEmpty()) {
            saveAddresses(addresses, company);
        }
        else if(addresses.isEmpty()){
            oldAddresss.forEach(a-> addressService.deleteById(a.getId()));
        }else {
            oldAddresss.forEach(a-> addressService.deleteById(a.getId()));
            saveAddresses(addresses, company);
        }
    }

    @SneakyThrows
    private void validateCompany(Company company) {
        if(company.getManufacturerCode() != null)
            manufacturerService.findById(company.getManufacturerCode().getId());

        if(company.isManufacturer())
            throw new IllegalArgumentException(MessageUtils.COMPANY_MANUFACTURER_ERROR.getDescription());

        if(company.isClient()&& (company.isProvider()|| company.isHolding()))
            throw new IllegalArgumentException(MessageUtils.COMPANY_PROVIDER_OR_HOLDING_ERROR.getDescription());

        if(company.isProvider()) {
            boolean validCpf = CpfCnpjValidator.isValidCNPJ(company.getCnpjCpf());
            company.setValidCgcCpf(validCpf);
            company.setCgcCpfFlag("F");
        }
        if(company.isClient()) {
            boolean validCnpj = CpfCnpjValidator.isValidCPF(company.getCnpjCpf());
            company.setValidCgcCpf(validCnpj);
            company.setCgcCpfFlag("J");
        }
    }

    private void validateExists(Company company) {
        if(companyRepository.findTopByCnpjCpfEquals(company.getCnpjCpf()).isPresent())
            throw new IllegalArgumentException(MessageUtils.COMPANY_CNPJ_REGISTERER_ERROR.getDescription());
    }

    private String verifyNewCompanyId() {
        Company lastCompany = companyRepository.findTopOrderByIdDesc();
        String lastId = lastCompany.getId();
        DecimalFormat df = new DecimalFormat("00000000000000");
        return df.format(new BigInteger(lastId).add(BigInteger.ONE));
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).title("Sucesso!").build();
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO updateCompany(CompanyDTO companyDTO) throws CompanyNotFoundException {
        verifyIfExists(companyDTO.getId());
        try {
            Company company = companyMapper.toModel(companyDTO);
            validateCompany(company);
            updateAddressess(companyDTO);
            companyRepository.save(company);
            return createMessageResponse(company.getId(), MessageUtils.COMPANY_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPANY_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] companyReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<CompanyDTO> companyDTOS  =  listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(companyDTOS== null || companyDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Empresas");
        parameters.put("column1", "EMPRESA");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = companyDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getCompanyName()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);
    }

    public MessageResponseDTO transferProvider(TransferProviderDTO transferProvider) throws CompanyNotFoundException {
        findById(transferProvider.getProviderId());
        unityRepository.findById(transferProvider.getBarcode()).orElseThrow(()-> new IllegalArgumentException(MessageUtils.UNITY_NOT_FOUND_BY_ID.getDescription() + transferProvider.getBarcode()));
        return nasphService.transferProvider(transferProvider);
    }

    public MessageResponseDTO instClientByProvider(ClientInstByProviderDTO clientInstByProviderDTO) throws CompanyNotFoundException {
        CompanyDTO client = findById(clientInstByProviderDTO.getClientId());
        if(clientInstByProviderDTO.getAddressId()!= null && client.getAddresses().stream().noneMatch(a -> a.getId().equals(clientInstByProviderDTO.getAddressId())))
            throw new IllegalArgumentException(MessageUtils.INSTA_CLIENT_PROVIDER_ADDRESS_ERROR.getDescription());
        return nasphService.instClientByProvider(clientInstByProviderDTO);
    }

    public PaginateResponseDTO<CompanyDTO> listAllClients(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            term = "";

        return PageableUtil.paginate(companyRepository.findAllClient(term.toUpperCase(Locale.ROOT).trim(), paging).map(companyMapper::toDTO));
    }

    public MessageResponseDTO instClientByTechnician(ClientInstByProviderDTO clientInstByProviderDTO) throws CompanyNotFoundException {
        CompanyDTO client = findById(clientInstByProviderDTO.getClientId());
        if(clientInstByProviderDTO.getAddressId()!= null && client.getAddresses().stream().noneMatch(a -> a.getId().equals(clientInstByProviderDTO.getAddressId())))
            throw new IllegalArgumentException(MessageUtils.INSTA_CLIENT_PROVIDER_ADDRESS_ERROR.getDescription());
        String provider = Utils.getUser().getCgcCpfCompany()!=null? Utils.getUser().getCgcCpfCompany().getId() : null;
        return nasphService.instClientByTechnician(clientInstByProviderDTO, provider, Utils.getUser().getId());
    }

    public byte[] emitProofProvider(EmitProofProviderDTO emitProofProviderDTO) throws CompanyNotFoundException, JRException, IOException {
        CompanyDTO companyDTO = findById(emitProofProviderDTO.getProviderId());
        if(emitProofProviderDTO.getResponsibleId()!=null && !emitProofProviderDTO.getResponsibleId().isEmpty())
            departmentRepository.findById(emitProofProviderDTO.getResponsibleId()).orElseThrow(()-> new IllegalArgumentException(MessageUtils.DEPARTMENT_NOT_FOUND_BY_ID.getDescription() + emitProofProviderDTO.getResponsibleId()));

        List<EmitProofProviderReportDTO> emitProofReportDTO = companyRepository.emitProofProvider(emitProofProviderDTO);
        if(emitProofReportDTO== null || emitProofReportDTO.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cnpj", companyDTO.getId());
        parameters.put("nome", companyDTO.getTradeName());
        return reportService.emitProofProviderReport(emitProofReportDTO, parameters);
    }

    public PaginateResponseDTO<CompanyDTO> listAllForUsersExtraction(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            term = "";

        return PageableUtil.paginate(companyRepository.findForUsersExtraction(term.toUpperCase(Locale.ROOT).trim(), paging).map(companyMapper::toDTO));
    
    }
}
