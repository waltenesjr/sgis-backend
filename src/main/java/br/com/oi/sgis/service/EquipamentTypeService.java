package br.com.oi.sgis.service;


import br.com.oi.sgis.dto.EquipamentTypeDTO;
import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.EquipamentType;
import br.com.oi.sgis.exception.EquipamentTypeNotFoundException;
import br.com.oi.sgis.mapper.EquipamentTypeMapper;
import br.com.oi.sgis.repository.EquipamentTypeRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import lombok.AllArgsConstructor;
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
public class EquipamentTypeService {

    private final EquipamentTypeRepository equipamentTypeRepository;
    private static final EquipamentTypeMapper equipamentTypeMapper = EquipamentTypeMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<EquipamentTypeDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(equipamentTypeRepository.findAll(paging).map(equipamentTypeMapper::toDTO));

        return PageableUtil.paginate(equipamentTypeRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(equipamentTypeMapper::toDTO));

    }

    public EquipamentTypeDTO findById(String id) throws EquipamentTypeNotFoundException {
        EquipamentType equipamentType = verifyIfExists(id);
        return equipamentTypeMapper.toDTO(equipamentType);
    }

    private EquipamentType verifyIfExists(String id) throws EquipamentTypeNotFoundException {
        return equipamentTypeRepository.findById(id)
                .orElseThrow(()-> new EquipamentTypeNotFoundException(MessageUtils.EQUIPAMENT_TYPE_NOT_FOUND_BY_ID.getDescription() + id) );
    }

    public byte[] equipamentTypeReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<EquipamentTypeDTO> equipamentTypeModelDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(equipamentTypeModelDTOS== null || equipamentTypeModelDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Tipos de Equipamentos");
        parameters.put("column1", "COD. TIPO");
        parameters.put("column2", "DESCRIÇÃO");
        parameters.put("column3", "COD. APLICACAO");

        List<GenericReportDTO> genericReport = equipamentTypeModelDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getEquipamentName()).data3(r.getApplication().getId()).build()
        ).collect(Collectors.toList());

        return reportService.genericReportThreeColumns(genericReport, parameters);

    }

    public void deleteById(String id) throws EquipamentTypeNotFoundException {
        verifyIfExists(id);
        try{
            equipamentTypeRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.EQUIPMENT_TYPE_DELETE_ERROR.getDescription());
        }
    }

    public MessageResponseDTO createEquipamentType(EquipamentTypeDTO equipamentTypeDTO) {
        Optional<EquipamentType> existEquipamentType = equipamentTypeRepository.findById(equipamentTypeDTO.getId());
        if(existEquipamentType.isPresent())
            throw new IllegalArgumentException(MessageUtils.EQUIPMENT_TYPE_ALREADY_EXISTS.getDescription());
        try {
            EquipamentType equipamentType = equipamentTypeMapper.toModel(equipamentTypeDTO);
            equipamentTypeRepository.save(equipamentType);
            return createMessageResponse(equipamentType.getId(), MessageUtils.EQUIPMENT_TYPE_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.EQUIPMENT_TYPE_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().title("Successo!").message(message + id).status(status).build();
    }

    public MessageResponseDTO updateEquipamentType(EquipamentTypeDTO equipamentTypeDTO) throws EquipamentTypeNotFoundException {
        verifyIfExists(equipamentTypeDTO.getId());
        try {
            EquipamentType equipamentType = equipamentTypeMapper.toModel(equipamentTypeDTO);
            equipamentTypeRepository.save(equipamentType);
            return createMessageResponse(equipamentType.getId(), MessageUtils.EQUIPMENT_TYPE_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.EQUIPMENT_TYPE_UPDATE_ERROR.getDescription());
        }
    }
}
