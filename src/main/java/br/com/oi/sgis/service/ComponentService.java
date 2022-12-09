package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Component;
import br.com.oi.sgis.entity.ComponentType;
import br.com.oi.sgis.exception.ComponentNotFoundException;
import br.com.oi.sgis.mapper.ComponentMapper;
import br.com.oi.sgis.mapper.ComponentTypeMapper;
import br.com.oi.sgis.repository.ComponentRepository;
import br.com.oi.sgis.repository.ComponentTypeRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ComponentService {

    private final ComponentRepository componentRepository;
    private static final ComponentMapper componentMapper = ComponentMapper.INSTANCE;
    private final ComponentTypeRepository componentTypeRepository;
    private final ReportService reportService;

    public PaginateResponseDTO<ComponentDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(componentRepository.findAll(paging).map(componentMapper::toDTO));

        return PageableUtil.paginate(componentRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(componentMapper::toDTO));
    }

    public ComponentDTO findById(String id) throws ComponentNotFoundException {
        Component component = verifyIfExists(id);
        return componentMapper.toDTO(component);
    }

    private Component verifyIfExists(String id) throws ComponentNotFoundException {
        return componentRepository.findById(id)
                .orElseThrow(()-> new ComponentNotFoundException(MessageUtils.COMPONENT_NOT_FOUND_BY_ID.getDescription() + id));
    }

    private ComponentType verifyIfExistsComponentType(String id) throws ComponentNotFoundException {
        return componentTypeRepository.findById(id)
                .orElseThrow(()-> new ComponentNotFoundException(MessageUtils.COMPONENT_TYPE_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createComponent(ComponentDTO componentDTO) throws ComponentNotFoundException {
        Optional<Component> existComponent = componentRepository.findById(componentDTO.getId());
        if(existComponent.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        verifyIfExistsComponentType(componentDTO.getComponentType().getId());
        try {
            Component component = componentMapper.toModel(componentDTO);
            componentRepository.save(component);
            return createMessageResponse(component.getId(), MessageUtils.COMPONENT_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPONENT_SAVE_ERROR.getDescription());
        }
    }

    public MessageResponseDTO updateComponent(ComponentDTO componentDTO) throws ComponentNotFoundException {
        verifyIfExists(componentDTO.getId());
        verifyIfExistsComponentType(componentDTO.getComponentType().getId());
        try {
            Component component = componentMapper.toModel(componentDTO);
            componentRepository.save(component);
            return createMessageResponse(component.getId(), MessageUtils.COMPONENT_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPONENT_UPDATE_ERROR.getDescription());
        }
    }

    public List<ComponentTypeDTO> componentTypes(){
        ComponentTypeMapper mapper = ComponentTypeMapper.INSTANCE;
        return componentTypeRepository.findAll(Sort.by("id")).parallelStream().map(mapper::toDTO).collect(Collectors.toList());
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public byte[] componentReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<ComponentDTO> componentDTOS =  listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(componentDTOS== null || componentDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Componentes");
        parameters.put("column1", "COMPONENTE");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = componentDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);
    }

    public void deleteById(String id) throws ComponentNotFoundException {
        verifyIfExists(id);
        try{
            componentRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPONENT_DELETE_ERROR.getDescription());
        }
    }
}
