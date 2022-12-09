package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Parameter;
import br.com.oi.sgis.entity.TechnicalStaff;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.exception.TechnicalStaffNotFoundException;
import br.com.oi.sgis.mapper.DepartmentMapper;
import br.com.oi.sgis.repository.DepartmentRepository;
import br.com.oi.sgis.repository.ParameterRepository;
import br.com.oi.sgis.repository.TechnicalStaffRepository;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final TechnicalStaffRepository technicalStaffRepository;
    private final ParameterRepository parameterRepository;
    private final ReportService reportService;
    private static final DepartmentMapper departmentMapper = DepartmentMapper.INSTANCE;

    public MessageResponseDTO createDepartment(DepartmentDTO departmentDTO){
        Department departmentToSave = departmentMapper.toModel(departmentDTO);
        validateDepartment(departmentToSave);
        try {
            Department savedDepartment = departmentRepository.save(departmentToSave);
            return createMessageResponse(savedDepartment.getId(), MessageUtils.DEPARTMENT_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DEPARTMENT_SAVE_ERROR.getDescription());
        }
    }

    @SneakyThrows
    private void validateDepartment(Department department) {
        validateSisFlag(department);
        validateRepairCenter(department);
        validateParameter(department);
        validateTech(department.getManager());
        validateTech(department.getContact());
    }

    private void validateParameter(Department department) {
        Optional<Parameter> parameter = parameterRepository.findTopByAbbreviationIsContaining(department.getId().substring(0, 2).toUpperCase(Locale.ROOT));
        if(parameter.isEmpty())
            throw  new IllegalArgumentException(MessageUtils.DEPARTMENT_SAVE_ERROR_PARAMETER.getDescription());
    }

    private void validateTech(TechnicalStaff technicalStaff) throws TechnicalStaffNotFoundException {
        if(technicalStaff !=null) {
            Optional<TechnicalStaff> manager =  technicalStaffRepository.findById(technicalStaff.getId());
            if(manager.isEmpty())
                throw new TechnicalStaffNotFoundException(MessageUtils.TECHNICAL_STAFF_NOT_FOUND_BY_ID.getDescription() + technicalStaff.getId());
        }
    }

    private void validateRepairCenter(Department department) {
        if(department.isRepairCenter() && (department.isObligateFowarding() || department.isNotDesignatedBloq()
                || department.isUnscreenedBlock() || department.isUnanalyzedLock()
                || department.isContractBudgetAnalysis())) {
            throw new IllegalArgumentException( MessageUtils.DEPARTMENT_SAVE_ERROR_INFOS.getDescription());
        }
    }

    private void validateSisFlag(Department department) {
        if(department.isSystemFlag()&&department.getResponsibleCenter()!=null)
            throw new IllegalArgumentException(MessageUtils.DEPARTMENT_SAVE_ERROR_RESPONSIBLE.getDescription());

    }

    public PaginateResponseDTO<DepartmentDTO> listPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term){
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> mapSort = DepartmentMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, mapSort));
        if(term.isBlank())
            return PageableUtil.paginate(departmentRepository.findAll(paging).map(departmentMapper::toDTO),mapSort);
        return PageableUtil.paginate(departmentRepository
                .findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(departmentMapper::toDTO),mapSort);
    }

    @SneakyThrows
    public DepartmentDTO findById(String id) throws DepartmentNotFoundException {
        Department department = verifyIfExists(id);
        return departmentMapper.toDTO(department);
    }
    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    private Department verifyIfExists(String id) throws DepartmentNotFoundException {
        return departmentRepository.findById(id)
                .orElseThrow(()-> new DepartmentNotFoundException(MessageUtils.DEPARTMENT_NOT_FOUND_BY_ID.getDescription() + id));
    }

    @SneakyThrows
    public MessageResponseDTO updateDepartment(DepartmentDTO departmentDTO) {
        verifyIfExists(departmentDTO.getId());
        Department departmentToSave = departmentMapper.toModel(departmentDTO);
        validateDepartment(departmentToSave);
        try{
            departmentRepository.save(departmentToSave);
            return createMessageResponse(departmentDTO.getId(), MessageUtils.DEPARTMENT_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DEPARTMENT_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] departmentReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<DepartmentDTO> departmentDTOS = listPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(departmentDTOS== null || departmentDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Áreas Administrativas");
        parameters.put("column1", "SIGLA");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = departmentDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public DepartmentDTO devolutionDepartmentByUnity(String id) {
        Optional<Department> department = departmentRepository.findDevolutionDepMostRecent(id);
        if(department.isEmpty())
            return null;
        return departmentMapper.toDTO(department.get());
    }

    public PaginateResponseDTO<DepartmentDTO> listAllForUsersExtraction(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> mapSort = DepartmentMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, mapSort));
        if(term.isBlank())
            term = "";
        return PageableUtil.paginate(departmentRepository
                .findForUsersExtraction(term.toUpperCase(Locale.ROOT).trim(), paging).map(departmentMapper::toDTO),mapSort);

    }

    public PaginateResponseDTO<DepartmentDTO> listAllRepairCenter(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> mapSort = DepartmentMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, mapSort));
        if(term.isBlank())
            term = "";
        return PageableUtil.paginate(departmentRepository
                .findAllRepairCenter(term.toUpperCase(Locale.ROOT).trim(), paging).map(departmentMapper::toDTO),mapSort);

    }
}
