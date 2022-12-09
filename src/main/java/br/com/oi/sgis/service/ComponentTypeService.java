package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ComponentTypeDTO;
import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.ComponentType;
import br.com.oi.sgis.exception.ComponentTypeNotFoundException;
import br.com.oi.sgis.mapper.ComponentTypeMapper;
import br.com.oi.sgis.repository.ComponentTypeRepository;
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
public class ComponentTypeService {
    private final ComponentTypeRepository componentTypeRepository;
    private static final ComponentTypeMapper componentTypeMapper = ComponentTypeMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<ComponentTypeDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(componentTypeRepository.findAll(paging).map(componentTypeMapper::toDTO));

        return PageableUtil.paginate(componentTypeRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(componentTypeMapper::toDTO));

    }

    public ComponentTypeDTO findById(String id) throws ComponentTypeNotFoundException {
        ComponentType componentType = verifyIfExists(id);
        return componentTypeMapper.toDTO(componentType);
    }

    private ComponentType verifyIfExists(String id) throws ComponentTypeNotFoundException {
        return componentTypeRepository.findById(id)
                .orElseThrow(()-> new ComponentTypeNotFoundException(MessageUtils.COMPONENT_TYPE_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createComponentType(ComponentTypeDTO componentTypeDTO) {
        Optional<ComponentType> existComponentType = componentTypeRepository.findById(componentTypeDTO.getId());
        if(existComponentType.isPresent())
            throw new IllegalArgumentException(MessageUtils.COMPONENT_TYPE_ALREADY_EXISTS.getDescription());
        try {
            ComponentType componentType = componentTypeMapper.toModel(componentTypeDTO);
            componentTypeRepository.save(componentType);
            return createMessageResponse(componentType.getId(), MessageUtils.COMPONENT_TYPE_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPONENT_TYPE_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateComponentType(ComponentTypeDTO componentTypeDTO) throws ComponentTypeNotFoundException {
        verifyIfExists(componentTypeDTO.getId());
        try {
            ComponentType componentType = componentTypeMapper.toModel(componentTypeDTO);
            componentTypeRepository.save(componentType);
            return createMessageResponse(componentType.getId(), MessageUtils.COMPONENT_TYPE_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPONENT_TYPE_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] componentTypeReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<ComponentTypeDTO> componentTypeDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(componentTypeDTOS== null || componentTypeDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Tipo de Componentes");
        parameters.put("column1", "CÓDIGO");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = componentTypeDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(String id) throws ComponentTypeNotFoundException {
        verifyIfExists(id);
        try{
            componentTypeRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPONENT_TYPE_DELETE_ERROR.getDescription());
        }
    }
}
