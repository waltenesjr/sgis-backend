package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.ModelTechnicianDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.ModelTechnician;
import br.com.oi.sgis.exception.ModelTechnicianNotFoundException;
import br.com.oi.sgis.mapper.ModelTechnicianMapper;
import br.com.oi.sgis.repository.ModelTechnicianRepository;
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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ModelTechnicianService {
    private final ModelTechnicianRepository modelTechnicianRepository;
    private static final ModelTechnicianMapper modelTechnicianMapper = ModelTechnicianMapper.INSTANCE;
    private final ReportService reportService;
    private final TechnicalStaffService technicalStaffService;
    private final AreaEquipamentService areaEquipamentService;

    public PaginateResponseDTO<ModelTechnicianDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = ModelTechnicianMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if (term == null || term.isBlank())
            term = "";
        String departmentId = Utils.getUser().getDepartmentCode().getId();
        return PageableUtil.paginate(modelTechnicianRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), departmentId, paging).map(modelTechnicianMapper::toDTO), sortMap);

    }

    public ModelTechnicianDTO findById(ModelTechnicianDTO modelTechnicianDTO) throws ModelTechnicianNotFoundException {
        ModelTechnician modelTechnician = verifyIfExists(modelTechnicianDTO);
        return modelTechnicianMapper.toDTO(modelTechnician);
    }

    private ModelTechnician verifyIfExists(ModelTechnicianDTO dto) throws ModelTechnicianNotFoundException {
        return modelTechnicianRepository.findById(dto.getModel().getId(), dto.getDepartment().getId(), dto.getTechnicalStaff().getId())
                .orElseThrow(()-> new ModelTechnicianNotFoundException(MessageUtils.MODEL_TS_NOT_FOUND_BY_ID.getDescription()));
    }

    public MessageResponseDTO createModelTechnician(ModelTechnicianDTO modelTechnicianDTO) {
        modelTechnicianDTO.setDepartment(Utils.getUser().getDepartmentCode());
        Optional<ModelTechnician> existModelTechnician = modelTechnicianRepository.findById( modelTechnicianDTO.getModel().getId()
                ,modelTechnicianDTO.getDepartment().getId(),modelTechnicianDTO.getTechnicalStaff().getId());
        if(existModelTechnician.isPresent())
            throw new IllegalArgumentException(MessageUtils.MODEL_TS_ALREADY_EXISTS.getDescription());
        validate(modelTechnicianDTO);
        try {
            ModelTechnician modelTechnician = modelTechnicianMapper.toModel(modelTechnicianDTO);
            modelTechnicianRepository.save(modelTechnician);
            return createMessageResponse(MessageUtils.MODEL_TS_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MODEL_TS_SAVE_ERROR.getDescription());
        }
    }

    @SneakyThrows
    private void validate(ModelTechnicianDTO modelTechnicianDTO) {
        technicalStaffService.findById(modelTechnicianDTO.getTechnicalStaff().getId());
        areaEquipamentService.findById(modelTechnicianDTO.getModel().getId());
    }

    private MessageResponseDTO createMessageResponse(String message, HttpStatus status) {
        return MessageResponseDTO.builder().title("Sucesso!").message(message).status(status).build();
    }

    public MessageResponseDTO updateModelTechnician(ModelTechnicianDTO modelTechnicianDTO) throws ModelTechnicianNotFoundException {
        verifyIfExists(modelTechnicianDTO);
        validate(modelTechnicianDTO);
        try {
            ModelTechnician modelTechnician = modelTechnicianMapper.toModel(modelTechnicianDTO);
            modelTechnicianRepository.save(modelTechnician);
            return createMessageResponse(MessageUtils.MODEL_TS_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MODEL_TS_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] modelTechnicianReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<ModelTechnicianDTO> modelTechnicianDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(modelTechnicianDTOS== null || modelTechnicianDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Modelos por Técnico");
        parameters.put("column1", "TECNICO");
        parameters.put("column2", "MODELO DE UNIDADE");
        parameters.put("column3", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = modelTechnicianDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getTechnicalStaff().getId())
                        .data2(r.getModel().getId()).data3(r.getModel().getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(ModelTechnicianDTO dto) throws ModelTechnicianNotFoundException {
        verifyIfExists(dto);
        try{
            ModelTechnician modelTechnician = modelTechnicianMapper.toModel(dto);
            modelTechnicianRepository.deleteById(modelTechnician.getId());
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MODEL_TS_DELETE_ERROR.getDescription());
        }
    }
}
