package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DiagnosisDTO;
import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Diagnosis;
import br.com.oi.sgis.exception.DiagnosisNotFoundException;
import br.com.oi.sgis.mapper.DiagnosisMapper;
import br.com.oi.sgis.repository.DiagnosisRepository;
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
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private static final DiagnosisMapper diagnosisMapper = DiagnosisMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<DiagnosisDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(diagnosisRepository.findAll(paging).map(diagnosisMapper::toDTO));

        return PageableUtil.paginate(diagnosisRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(diagnosisMapper::toDTO));

    }

    public DiagnosisDTO findById(String id) throws DiagnosisNotFoundException {
        Diagnosis diagnosis = verifyIfExists(id);
        return diagnosisMapper.toDTO(diagnosis);
    }

    private Diagnosis verifyIfExists(String id) throws DiagnosisNotFoundException {
        return diagnosisRepository.findById(id)
                .orElseThrow(()-> new DiagnosisNotFoundException(MessageUtils.DIAGNOSIS_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createDiagnosis(DiagnosisDTO diagnosisDTO) {
        Optional<Diagnosis> existDiagnosis = diagnosisRepository.findById(diagnosisDTO.getId());
        if(existDiagnosis.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        try {
            Diagnosis diagnosis = diagnosisMapper.toModel(diagnosisDTO);
            diagnosisRepository.save(diagnosis);
            return createMessageResponse(diagnosis.getId(), MessageUtils.DIAGNOSIS_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DIAGNOSIS_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateDiagnosis(DiagnosisDTO diagnosisDTO) throws DiagnosisNotFoundException {
        verifyIfExists(diagnosisDTO.getId());
        try {
            Diagnosis diagnosis = diagnosisMapper.toModel(diagnosisDTO);
            diagnosisRepository.save(diagnosis);
            return createMessageResponse(diagnosis.getId(), MessageUtils.DIAGNOSIS_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DIAGNOSIS_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] diagnosisReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<DiagnosisDTO> diagnosisDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(diagnosisDTOS== null || diagnosisDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Diagnósticos");
        parameters.put("column1", "CÓDIGO");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = diagnosisDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(String id) throws DiagnosisNotFoundException {
        verifyIfExists(id);
        try{
            diagnosisRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DIAGNOSIS_DELETE_ERROR.getDescription());
        }
    }
}
