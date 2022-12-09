package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ContractDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Contract;
import br.com.oi.sgis.entity.ModelContract;
import br.com.oi.sgis.exception.ContractNotFoundException;
import br.com.oi.sgis.mapper.ContractMapper;
import br.com.oi.sgis.repository.ContractRepository;
import br.com.oi.sgis.repository.ModelContractRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
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
import java.util.*;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ContractService {
    private final ContractRepository contractRepository;
    private static final ContractMapper contractMapper = ContractMapper.INSTANCE;
    private final ReportService reportService;
    private final CompanyService companyService;
    private final DepartmentService departmentService;
    private final ModelContractRepository modelContractRepository;

    public PaginateResponseDTO<ContractDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(contractRepository.findAll(paging).map(contractMapper::toDTO));

        return PageableUtil.paginate(contractRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(contractMapper::toDTO));
    }

    public ContractDTO findById(String id) throws ContractNotFoundException {
        Contract contract = verifyIfExists(id);
        return contractMapper.toDTO(contract);
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    private Contract verifyIfExists(String id) throws ContractNotFoundException {
        return contractRepository.findById(id)
                .orElseThrow(()-> new ContractNotFoundException(MessageUtils.CONTRACT_NOT_FOUND_BY_ID.getDescription() + id));
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO createContract(ContractDTO contractDTO) {
        Optional<Contract> existContract = contractRepository.findById(contractDTO.getId());
        if(existContract.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        validate(contractDTO);
        if(!contractDTO.isAcquisition() && !contractDTO.isMaintenance()){
            contractDTO.setAcquisition(true);
            contractDTO.setMaintenance(true);
        }
        try {
            Contract contract = contractMapper.toModel(contractDTO);
            contractRepository.save(contract);
            saveModelContracts(contract.getModelContracts());
            return createMessageResponse(contract.getId(), MessageUtils.CONTRACT_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.CONTRACT_SAVE_ERROR.getDescription());
        }
    }

    @SneakyThrows
    private void validate(ContractDTO contract) {
        companyService.findById(contract.getCompany().getId());
        departmentService.findById(contract.getDepartment().getId());
    }

    public MessageResponseDTO updateContract(ContractDTO contractDTO) throws ContractNotFoundException {
        verifyIfExists(contractDTO.getId());
        validate(contractDTO);
        try {
            Contract contract = contractMapper.toModel(contractDTO);
            updateModelContracts(contract);
            contractRepository.save(contract);
            return createMessageResponse(contract.getId(), MessageUtils.CONTRACT_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.CONTRACT_UPDATE_ERROR.getDescription());
        }
    }

    private void saveModelContracts(List<ModelContract> modelContracts) {
        try {
            if(modelContracts !=null && !modelContracts.isEmpty())
                modelContractRepository.saveAll(modelContracts);
        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.MODEL_CONTRACT_SAVE_ERROR.getDescription());
        }

    }

    private void updateModelContracts(Contract contract) throws ContractNotFoundException {
        List<ModelContract> modelContracts = contract.getModelContracts();
        List<ModelContract> oldModelContracts = verifyIfExists(contract.getId()).getModelContracts();
        if(modelContracts.isEmpty() && oldModelContracts.isEmpty())
            return;
        if(oldModelContracts.isEmpty()) {
            saveModelContracts(modelContracts);
        }
        else if(modelContracts.isEmpty()){
            modelContractRepository.deleteAll(oldModelContracts);
        }else {
            modelContractRepository.deleteAll(oldModelContracts);
            saveModelContracts(modelContracts);
        }
    }


    public byte[] contractReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<ContractDTO> contractDTOS =  listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(contractDTOS== null || contractDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        Map<String, Object> parameters = new HashMap<>();
        return reportService.fillContractReport(contractDTOS, parameters);
    }

    public void deleteById(String id) throws ContractNotFoundException {
        verifyIfExists(id);
        try{
            contractRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.CONTRACT_DELETE_ERROR.getDescription());
        }
    }

    public PaginateResponseDTO<ContractDTO> listForwardRepair(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            term = "";

        return PageableUtil.paginate(contractRepository.listForwardRepair(term.toUpperCase(Locale.ROOT).trim(), paging).map(contractMapper::toDTO));
    }
}
