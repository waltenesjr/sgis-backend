package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DepartmentUnityDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.DepartmentUnity;
import br.com.oi.sgis.entity.DepartmentUnityID;
import br.com.oi.sgis.exception.DepartmentUnityNotFoundException;
import br.com.oi.sgis.mapper.DepartmentUnityMapper;
import br.com.oi.sgis.repository.DepartmentUnityRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import br.com.oi.sgis.util.Utils;
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

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DepartmentUnityService {
    private final DepartmentUnityRepository departmentUnityRepository;
    private final DepartmentService departmentService;
    private final AreaEquipamentService areaEquipamentService;
    private static final DepartmentUnityMapper departmentUnityMapper = DepartmentUnityMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<DepartmentUnityDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = DepartmentUnityMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if (term.isBlank())
            return PageableUtil.paginate(departmentUnityRepository.findAll(paging).map(departmentUnityMapper::toDTO), sortMap);

        return PageableUtil.paginate(departmentUnityRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(departmentUnityMapper::toDTO), sortMap);

    }

    public DepartmentUnityDTO findByIdDTO(DepartmentUnityDTO dto) throws DepartmentUnityNotFoundException {
        DepartmentUnityID id = departmentUnityMapper.toModel(dto).getId();
        DepartmentUnity departmentUnity = verifyIfExists(id);
        return departmentUnityMapper.toDTO(departmentUnity);
    }

    private DepartmentUnity verifyIfExists(DepartmentUnityID id) throws DepartmentUnityNotFoundException {
        return departmentUnityRepository.findById(id)
                .orElseThrow(()-> new DepartmentUnityNotFoundException(MessageUtils.DEP_UNITY_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createDepartmentUnity(DepartmentUnityDTO departmentUnityDTO) {
        validateDepartmentUser(departmentUnityDTO);
        return create(departmentUnityDTO);
    }

    public MessageResponseDTO createDepartmentUnityAdmin(DepartmentUnityDTO departmentUnityDTO) {
        return create(departmentUnityDTO);
    }

    private MessageResponseDTO create(DepartmentUnityDTO departmentUnityDTO) {
        validate(departmentUnityDTO);
        DepartmentUnity departmentUnity = departmentUnityMapper.toModel(departmentUnityDTO);
        Optional<DepartmentUnity> existDepartmentUnity = departmentUnityRepository.findById(departmentUnity.getId());
        if (existDepartmentUnity.isPresent())
            throw new IllegalArgumentException(MessageUtils.DEP_UNITY_ALREADY_REGISTERED.getDescription());
        try {
            departmentUnityRepository.save(departmentUnity);
            return createMessageResponse(MessageUtils.DEP_UNITY_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.DEP_UNITY_SAVE_ERROR.getDescription());
        }
    }

    public MessageResponseDTO updateLocationUnities(DepartmentUnityDTO departmentUnityDTO) throws DepartmentUnityNotFoundException {
        DepartmentUnity departmentUnity = departmentUnityMapper.toModel(departmentUnityDTO);
        validate(departmentUnityDTO);
        verifyIfExists(departmentUnity.getId());
        try {
            departmentUnityRepository.updateUnitiesByDepartmentUnity(departmentUnity);
            return createMessageResponse(MessageUtils.DEP_UNITY_UPDATE_LOC_SUCCESS.getDescription(), HttpStatus.OK);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.DEP_UNITY_UPDATE_LOC_ERROR.getDescription());
        }
    }

    @SneakyThrows
    private void validate(DepartmentUnityDTO departmentUnityDTO) {
        departmentService.findById(departmentUnityDTO.getDepartment().getId());
        areaEquipamentService.findById(departmentUnityDTO.getModelUnity().getId());
    }

    private void validateDepartmentUser(DepartmentUnityDTO departmentUnityDTO) {
        if(!Utils.getUser().getDepartmentCode().getId().equals(departmentUnityDTO.getDepartment().getId())){
            throw new IllegalArgumentException(MessageUtils.DEP_UNITY_DIFFERENT_USER_DEP.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message).status(status).build();
    }

    public MessageResponseDTO updateDepartmentUnity(DepartmentUnityDTO departmentUnityDTO) throws DepartmentUnityNotFoundException {
        validateDepartmentUser(departmentUnityDTO);
        return update(departmentUnityDTO);
    }

    public MessageResponseDTO updateDepartmentUnityAdmin(DepartmentUnityDTO departmentUnityDTO) throws DepartmentUnityNotFoundException {
        return update(departmentUnityDTO);
    }

    private MessageResponseDTO update(DepartmentUnityDTO departmentUnityDTO) throws DepartmentUnityNotFoundException {
        validate(departmentUnityDTO);
        DepartmentUnity departmentUnity = departmentUnityMapper.toModel(departmentUnityDTO);
        verifyIfExists(departmentUnity.getId());
        try {
            departmentUnityRepository.save(departmentUnity);
            return createMessageResponse(MessageUtils.DEP_UNITY_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.DEP_UNITY_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] departmentUnityReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<DepartmentUnityDTO> departmentUnityDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(departmentUnityDTOS== null || departmentUnityDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        return reportService.fillDepartmentUnityReport(departmentUnityDTOS, parameters);

    }

    public void deleteById(DepartmentUnityDTO dto) throws DepartmentUnityNotFoundException {
        validate(dto);
        validateDepartmentUser(dto);
        DepartmentUnity departmentUnity = departmentUnityMapper.toModel(dto);
        verifyIfExists(departmentUnity.getId());
        try{
            departmentUnityRepository.deleteById(departmentUnity.getId());
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DEP_UNITY_DELETE_ERROR.getDescription());
        }
    }
}
