package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Central;
import br.com.oi.sgis.exception.CentralNotFoundException;
import br.com.oi.sgis.mapper.CentralMapper;
import br.com.oi.sgis.repository.CentralRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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
public class CentralService {

    private final CentralRepository centralRepository;
    private static final CentralMapper centralMapper = CentralMapper.INSTANCE;
    private final ReportService reportService;
    private final StationService stationService;

    public PaginateResponseDTO<CentralDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(centralRepository.findAll(paging).map(centralMapper::toDTO));

        return PageableUtil.paginate(centralRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(centralMapper::toDTO));
    }

    public CentralDTO findById(String id) throws CentralNotFoundException {
        Central central = verifyIfExists(id);
        return centralMapper.toDTO(central);
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    private Central verifyIfExists(String id) throws CentralNotFoundException {
        return centralRepository.findById(id)
                .orElseThrow(()-> new CentralNotFoundException(MessageUtils.CENTRAL_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createCentral(CentralDTO centralDTO) {
        Optional<Central> existCentral = centralRepository.findById(centralDTO.getId());
        if(existCentral.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        validateStation(centralDTO.getStation());
        try {
            Central central = centralMapper.toModel(centralDTO);
            central.setActiveFlag(true);
            centralRepository.save(central);
            return createMessageResponse(central.getId(), MessageUtils.CENTRAL_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.CENTRAL_SAVE_ERROR.getDescription());
        }
    }

    @SneakyThrows
    private void validateStation(StationDTO stationDTO) {
        stationService.findById(stationDTO.getId());
    }

    public MessageResponseDTO updateCentral(CentralDTO centralDTO) throws CentralNotFoundException {
        verifyIfExists(centralDTO.getId());
        validateStation(centralDTO.getStation());
        try {
            Central central = centralMapper.toModel(centralDTO);
            centralRepository.save(central);
            return createMessageResponse(central.getId(), MessageUtils.CENTRAL_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.CENTRAL_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] centralReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<CentralDTO> centralDTOS  =  listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();

        if(centralDTOS== null || centralDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Agrupamento de equipamentos");
        parameters.put("column1", "CENTRAL");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = centralDTOS.stream().map(c ->
                GenericReportDTO.builder().data1(c.getId()).data2(c.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);
    }

    public void deleteById(String id) throws CentralNotFoundException {
        verifyIfExists(id);
        try{
            centralRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.CENTRAL_DELETE_ERROR.getDescription());
        }
    }
}
