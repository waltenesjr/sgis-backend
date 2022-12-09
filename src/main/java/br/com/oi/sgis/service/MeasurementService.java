package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MeasurementDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Measurement;
import br.com.oi.sgis.exception.MeasurementNotFoundException;
import br.com.oi.sgis.mapper.MeasurementMapper;
import br.com.oi.sgis.repository.MeasurementRepository;
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
public class MeasurementService {
    private final MeasurementRepository measurementRepository;
    private static final MeasurementMapper measurementMapper = MeasurementMapper.INSTANCE;
    private final ReportService reportService;

    public List<MeasurementDTO> listAll() {
        List<Measurement> allMeasurements = measurementRepository.findAll();
        return allMeasurements.parallelStream().map(measurementMapper::toDTO).collect(Collectors.toList());
    }

    public MeasurementDTO findById(String id) throws MeasurementNotFoundException {
        Measurement measurementerty = verifyIfExists(id);
        return measurementMapper.toDTO(measurementerty);
    }

    private Measurement verifyIfExists(String id) throws MeasurementNotFoundException {
        return measurementRepository.findById(id)
                .orElseThrow(()-> new MeasurementNotFoundException(MessageUtils.MEASUREMENT_NOT_FOUND_BY_ID.getDescription() + id) );
    }

    public PaginateResponseDTO<MeasurementDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(measurementRepository.findAll(paging).map(measurementMapper::toDTO));

        return PageableUtil.paginate(measurementRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(measurementMapper::toDTO));

    }
    public MessageResponseDTO createMeasurement(MeasurementDTO measurementDTO) {
        Optional<Measurement> existMeasurement = measurementRepository.findById(measurementDTO.getId());
        if(existMeasurement.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        try {
            Measurement measurement = measurementMapper.toModel(measurementDTO);
            measurementRepository.save(measurement);
            return createMessageResponse(measurement.getId(), MessageUtils.MEASUREMENT_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MEASUREMENT_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateMeasurement(MeasurementDTO measurementDTO) throws MeasurementNotFoundException {
        verifyIfExists(measurementDTO.getId());
        try {
            Measurement measurement = measurementMapper.toModel(measurementDTO);
            measurementRepository.save(measurement);
            return createMessageResponse(measurement.getId(), MessageUtils.MEASUREMENT_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MEASUREMENT_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] measurementReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<MeasurementDTO> measurementDTOS =  listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(measurementDTOS== null || measurementDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Unidade de Medidas");
        parameters.put("column1", "CODIGO");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = measurementDTOS.stream().map(d ->
                GenericReportDTO.builder().data1(d.getId()).data2(d.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(String id) throws MeasurementNotFoundException {
        verifyIfExists(id);
        try{
            measurementRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MEASUREMENT_DELETE_ERROR.getDescription());
        }
    }
}
