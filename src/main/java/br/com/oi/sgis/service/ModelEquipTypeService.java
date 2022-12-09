package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.ModelEquipTypeDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.ModelEquipamentType;
import br.com.oi.sgis.exception.ModelEquipTypeNotFound;
import br.com.oi.sgis.mapper.ModelEquipTypeMapper;
import br.com.oi.sgis.repository.ModelEquipTypeRepository;
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
public class ModelEquipTypeService {

    private final ModelEquipTypeRepository modelEquipTypeRepository;
    private static final ModelEquipTypeMapper modelEquipTypeMapper = ModelEquipTypeMapper.INSTANCE;
    private final ReportService reportService;
    private final TechnologyService technologyService;
    private final TechnicalStaffService technicalStaffService;
    private final CompanyService companyService;
    private final EquipamentTypeService equipamentTypeService;

    public PaginateResponseDTO<ModelEquipTypeDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if(term.isBlank())
            return PageableUtil.paginate(modelEquipTypeRepository.findAll(paging).map(modelEquipTypeMapper::toDTO));

        return PageableUtil.paginate( modelEquipTypeRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(modelEquipTypeMapper::toDTO));
    }

    public ModelEquipTypeDTO findById(String id) throws ModelEquipTypeNotFound {
        ModelEquipamentType modelEquipamentType = verifyIfExists(id);
        return modelEquipTypeMapper.toDTO(modelEquipamentType);
    }

    private ModelEquipamentType verifyIfExists(String id) throws ModelEquipTypeNotFound {
        return modelEquipTypeRepository.findById(id)
                .orElseThrow(()-> new ModelEquipTypeNotFound(MessageUtils.MODEL_EQUIPAMENT_TYPE_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO updateDescontEquip(ModelEquipTypeDTO modelEquipTypeDTO) throws ModelEquipTypeNotFound {
        verifyIfExists(modelEquipTypeDTO.getId());
        try {
            modelEquipTypeRepository.updateEquipByModelDesct(modelEquipTypeDTO.getId(), Utils.getUser().getId());
            return createMessageResponse(modelEquipTypeDTO.getId(), MessageUtils.MODEL_EQUIPAMENT_TYPE_DESC_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MODEL_EQUIPAMENT_TYPE_DESC_ERROR.getDescription());
        }
    }

    @SneakyThrows
    private void validate(ModelEquipTypeDTO dto){
        technologyService.findById(dto.getTechnology().getId());
        equipamentTypeService.findById(dto.getEquipamentType().getId());
        companyService.findById(dto.getCompany().getId());
        if(dto.getTechnician()!=null)
            technicalStaffService.findById(dto.getTechnician().getId());
    }
    public MessageResponseDTO createModelEquipType(ModelEquipTypeDTO modelEquipTypeDTO) {
        validate(modelEquipTypeDTO);
        Optional<ModelEquipamentType> existModelEquipType = modelEquipTypeRepository.findById(modelEquipTypeDTO.getId());
        if(existModelEquipType.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        try {
            ModelEquipamentType modelEquipType = modelEquipTypeMapper.toModel(modelEquipTypeDTO);
            modelEquipTypeRepository.save(modelEquipType);
            return createMessageResponse(modelEquipType.getId(), MessageUtils.MODEL_EQUIPAMENT_TYPE_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MODEL_EQUIPAMENT_TYPE_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateModelEquipType(ModelEquipTypeDTO modelEquipTypeDTO) throws ModelEquipTypeNotFound {
        verifyIfExists(modelEquipTypeDTO.getId());
        validate(modelEquipTypeDTO);
        try {
            ModelEquipamentType modelEquipType = modelEquipTypeMapper.toModel(modelEquipTypeDTO);
            modelEquipTypeRepository.save(modelEquipType);
            return createMessageResponse(modelEquipType.getId(), MessageUtils.MODEL_EQUIPAMENT_TYPE_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MODEL_EQUIPAMENT_TYPE_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] modelEquipTypeReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<ModelEquipTypeDTO> modelEquipTypeDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(modelEquipTypeDTOS== null || modelEquipTypeDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();

        return reportService.fillModelEquipamentReport(modelEquipTypeDTOS, parameters);

    }

    public void deleteById(String id) throws ModelEquipTypeNotFound {
        verifyIfExists(id);
        try{
            modelEquipTypeRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MODEL_EQUIPAMENT_TYPE_DELETE_ERROR.getDescription());
        }
    }
}
