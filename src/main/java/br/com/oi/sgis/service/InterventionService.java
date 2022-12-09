package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.InterventionDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Intervention;
import br.com.oi.sgis.exception.InterventionNotFoundException;
import br.com.oi.sgis.mapper.InterventionMapper;
import br.com.oi.sgis.repository.InterventionRepository;
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
public class InterventionService {

    private final InterventionRepository interventionRepository;
    private static final InterventionMapper interventionMapper = InterventionMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<InterventionDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(interventionRepository.findAll(paging).map(interventionMapper::toDTO));

        return PageableUtil.paginate(interventionRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(interventionMapper::toDTO));

    }

    public InterventionDTO findById(String id) throws InterventionNotFoundException {
        Intervention intervention = verifyIfExists(id);
        return interventionMapper.toDTO(intervention);
    }

    private Intervention verifyIfExists(String id) throws InterventionNotFoundException {
        return interventionRepository.findById(id)
                .orElseThrow(()-> new InterventionNotFoundException(MessageUtils.INTERVENTION_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createIntervention(InterventionDTO interventionDTO) {
        Optional<Intervention> existIntervention = interventionRepository.findById(interventionDTO.getId());
        if(existIntervention.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        try {
            Intervention intervention = interventionMapper.toModel(interventionDTO);
            defaultValues(intervention);
            interventionRepository.save(intervention);
            return createMessageResponse(intervention.getId(), MessageUtils.INTERVENTION_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.INTERVENTION_SAVE_ERROR.getDescription());
        }
    }

    private void defaultValues(Intervention intervention) {
        if(intervention.getCalibrationFlag() == null)
            intervention.setCalibrationFlag(false);
        if(intervention.getExternalFlag() == null)
            intervention.setExternalFlag(true);
        if(intervention.getInternalFlag() == null)
            intervention.setInternalFlag(true);
        if(intervention.getProductiveFlag() == null)
            intervention.setProductiveFlag(true);
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).title("Sucesso!").build();
    }

    public MessageResponseDTO updateIntervention(InterventionDTO interventionDTO) throws InterventionNotFoundException {
        verifyIfExists(interventionDTO.getId());
        try {
            Intervention intervention = interventionMapper.toModel(interventionDTO);
            interventionRepository.save(intervention);
            return createMessageResponse(intervention.getId(), MessageUtils.INTERVENTION_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.INTERVENTION_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] interventionReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<InterventionDTO> interventionDTOS  =  listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(interventionDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Intervenções");
        parameters.put("column1", "CÓDIGO");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = interventionDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(String id) throws InterventionNotFoundException {
        verifyIfExists(id);
        try{
            interventionRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.INTERVENTION_DELETE_ERROR.getDescription());
        }
    }
}
