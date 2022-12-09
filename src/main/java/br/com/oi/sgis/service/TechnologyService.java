package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.TechnologyDTO;
import br.com.oi.sgis.entity.Technology;
import br.com.oi.sgis.exception.TechnologyNotFoundException;
import br.com.oi.sgis.mapper.TechnologyMapper;
import br.com.oi.sgis.repository.TechnologyRepository;
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
public class TechnologyService {
    private final TechnologyRepository technologyRepository;
    private static final TechnologyMapper technologyMapper = TechnologyMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<TechnologyDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(technologyRepository.findAll(paging).map(technologyMapper::toDTO));

        return PageableUtil.paginate(technologyRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(technologyMapper::toDTO));

    }

    public TechnologyDTO findById(String id) throws TechnologyNotFoundException {
        Technology technology = verifyIfExists(id);
        return technologyMapper.toDTO(technology);
    }

    private Technology verifyIfExists(String id) throws TechnologyNotFoundException {
        return technologyRepository.findById(id)
                .orElseThrow(()-> new TechnologyNotFoundException(MessageUtils.TECHNOLOGY_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createTechnology(TechnologyDTO technologyDTO) {
        Optional<Technology> existTechnology = technologyRepository.findById(technologyDTO.getId());
        if(existTechnology.isPresent())
            throw new IllegalArgumentException(MessageUtils.TECHNOLOGY_ALREADY_EXISTS.getDescription());
        try {
            Technology technology = technologyMapper.toModel(technologyDTO);
            technologyRepository.save(technology);
            return createMessageResponse(technology.getId(), MessageUtils.TECHNOLOGY_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TECHNOLOGY_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().title("Sucesso!").message(message + id).status(status).build();
    }

    public MessageResponseDTO updateTechnology(TechnologyDTO technologyDTO) throws TechnologyNotFoundException {
        verifyIfExists(technologyDTO.getId());
        try {
            Technology technology = technologyMapper.toModel(technologyDTO);
            technologyRepository.save(technology);
            return createMessageResponse(technology.getId(), MessageUtils.TECHNOLOGY_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TECHNOLOGY_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] technologyReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<TechnologyDTO> technologyDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(technologyDTOS== null || technologyDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Tecnologias");
        parameters.put("column1", "TECNOLOGIA");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = technologyDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(String id) throws TechnologyNotFoundException {
        verifyIfExists(id);
        try{
            technologyRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TECHNOLOGY_DELETE_ERROR.getDescription());
        }
    }
}
