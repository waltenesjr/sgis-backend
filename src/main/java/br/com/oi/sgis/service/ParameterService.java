package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ParameterDTO;
import br.com.oi.sgis.entity.Parameter;
import br.com.oi.sgis.exception.ParameterNotFoundException;
import br.com.oi.sgis.mapper.ParameterMapper;
import br.com.oi.sgis.repository.ParameterRepository;
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

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ParameterService {
    private final ParameterRepository parameterRepository;
    private static final ParameterMapper parameterMapper = ParameterMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<ParameterDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(parameterRepository.findAll(paging).map(parameterMapper::toDTO));

        return PageableUtil.paginate(parameterRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(parameterMapper::toDTO));

    }

    public ParameterDTO findById(String id) throws ParameterNotFoundException {
        Parameter parameter = verifyIfExists(id);
        return parameterMapper.toDTO(parameter);
    }

    private Parameter verifyIfExists(String id) throws ParameterNotFoundException {
        return parameterRepository.findById(id)
                .orElseThrow(()-> new ParameterNotFoundException(MessageUtils.PARAMETER_NOT_FOUND_BY_ID.getDescription() + id));
    }


    public MessageResponseDTO createParameter(ParameterDTO parameterDTO) {
        Optional<Parameter> existParameter = parameterRepository.findById(parameterDTO.getId());
        if(existParameter.isPresent())
            throw new IllegalArgumentException(MessageUtils.PARAMETER_ALREADY_EXISTS.getDescription());
        try {
            Parameter parameter = parameterMapper.toModel(parameterDTO);
            parameterRepository.save(parameter);
            return createMessageResponse(parameter.getId(), MessageUtils.PARAMETER_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.PARAMETER_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().title("Sucesso!").message(message + id).status(status).build();
    }

    public MessageResponseDTO updateParameter(ParameterDTO parameterDTO) throws ParameterNotFoundException {
        verifyIfExists(parameterDTO.getId());
        try {
            Parameter parameter = parameterMapper.toModel(parameterDTO);
            parameterRepository.save(parameter);
            return createMessageResponse(parameter.getId(), MessageUtils.PARAMETER_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.PARAMETER_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] parameterReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<ParameterDTO> parameterDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(parameterDTOS== null || parameterDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        return reportService.fillParameterReport(parameterDTOS, parameters);

    }

    public void deleteById(String id) throws ParameterNotFoundException {
        verifyIfExists(id);
        try{
            parameterRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.PARAMETER_DELETE_ERROR.getDescription());
        }
    }
}
