package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DefectDTO;
import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Defect;
import br.com.oi.sgis.exception.DefectNotFoundException;
import br.com.oi.sgis.mapper.DefectMapper;
import br.com.oi.sgis.repository.DefectRepository;
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
public class DefectService {

    private final DefectRepository defectRepository;
    private static final DefectMapper defectMapper = DefectMapper.INSTANCE;
    private final ReportService reportService;

    public List<DefectDTO> listAll() {
        List<Defect> allDefects = defectRepository.findAll();
        return allDefects.parallelStream().map(defectMapper::toDTO).collect(Collectors.toList());
    }

    public PaginateResponseDTO<DefectDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(defectRepository.findAll(paging).map(defectMapper::toDTO));

        return PageableUtil.paginate(defectRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(defectMapper::toDTO));

    }

    public DefectDTO findById(String id) throws DefectNotFoundException {
        Defect defect = verifyIfExists(id);
        return defectMapper.toDTO(defect);
    }

    private Defect verifyIfExists(String id) throws DefectNotFoundException {
        return defectRepository.findById(id)
                .orElseThrow(()-> new DefectNotFoundException(MessageUtils.DEFECT_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createDefect(DefectDTO defectDTO) {
        Optional<Defect> existDefect = defectRepository.findById(defectDTO.getId());
        if(existDefect.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        try {
            Defect defect = defectMapper.toModel(defectDTO);
            defectRepository.save(defect);
            return createMessageResponse(defect.getId(), MessageUtils.DEFECT_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DEFECT_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateDefect(DefectDTO defectDTO) throws DefectNotFoundException {
        verifyIfExists(defectDTO.getId());
        try {
            Defect defect = defectMapper.toModel(defectDTO);
            defectRepository.save(defect);
            return createMessageResponse(defect.getId(), MessageUtils.DEFECT_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DEFECT_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] defectReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<DefectDTO> defectDTOS =  listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(defectDTOS== null || defectDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Defeitos");
        parameters.put("column1", "DEFEITO");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = defectDTOS.stream().map(d ->
                GenericReportDTO.builder().data1(d.getId()).data2(d.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(String id) throws DefectNotFoundException {
        verifyIfExists(id);
        try{
            defectRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DEFECT_DELETE_ERROR.getDescription());
        }
    }
}
