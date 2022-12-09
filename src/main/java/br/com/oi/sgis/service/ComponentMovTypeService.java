package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ComponentMovTypeDTO;
import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.ComponentMovType;
import br.com.oi.sgis.exception.ComponentMovTypeNotFoundException;
import br.com.oi.sgis.mapper.ComponentMovTypeMapper;
import br.com.oi.sgis.repository.ComponentMovTypeRepository;
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
public class ComponentMovTypeService {
    private final ComponentMovTypeRepository componentMovTypeRepository;
    private static final ComponentMovTypeMapper componentMovTypeMapper = ComponentMovTypeMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<ComponentMovTypeDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(componentMovTypeRepository.findAll(paging).map(componentMovTypeMapper::toDTO));

        return PageableUtil.paginate(componentMovTypeRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(componentMovTypeMapper::toDTO));

    }

    public ComponentMovTypeDTO findById(String id) throws ComponentMovTypeNotFoundException {
        ComponentMovType componentMovType = verifyIfExists(id);
        return componentMovTypeMapper.toDTO(componentMovType);
    }

    private ComponentMovType verifyIfExists(String id) throws ComponentMovTypeNotFoundException {
        return componentMovTypeRepository.findById(id)
                .orElseThrow(()-> new ComponentMovTypeNotFoundException(MessageUtils.COMPONENT_MOV_TYPE_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createComponentMovType(ComponentMovTypeDTO componentMovTypeDTO) {
        Optional<ComponentMovType> existComponentMovType = componentMovTypeRepository.findById(componentMovTypeDTO.getId());
        if(existComponentMovType.isPresent())
            throw new IllegalArgumentException(MessageUtils.COMPONENT_MOV_TYPE_ALREADY_EXISTS.getDescription());
        try {
            ComponentMovType componentMovType = componentMovTypeMapper.toModel(componentMovTypeDTO);
            componentMovTypeRepository.save(componentMovType);
            return createMessageResponse(componentMovType.getId(), MessageUtils.COMPONENT_MOV_TYPE_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPONENT_MOV_TYPE_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateComponentMovType(ComponentMovTypeDTO componentMovTypeDTO) throws ComponentMovTypeNotFoundException {
        verifyIfExists(componentMovTypeDTO.getId());
        try {
            ComponentMovType componentMovType = componentMovTypeMapper.toModel(componentMovTypeDTO);
            componentMovTypeRepository.save(componentMovType);
            return createMessageResponse(componentMovType.getId(), MessageUtils.COMPONENT_MOV_TYPE_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPONENT_MOV_TYPE_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] componentMovTypeReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<ComponentMovTypeDTO> componentMovTypeDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(componentMovTypeDTOS== null || componentMovTypeDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Tipo de Mov. Componentes");
        parameters.put("column1", "CÓDIGO");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = componentMovTypeDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(String id) throws ComponentMovTypeNotFoundException {
        verifyIfExists(id);
        try{
            componentMovTypeRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPONENT_MOV_TYPE_DELETE_ERROR.getDescription());
        }
    }
}
