package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.TimeDTO;
import br.com.oi.sgis.entity.Time;
import br.com.oi.sgis.entity.TimeID;
import br.com.oi.sgis.exception.TimeNotFoundException;
import br.com.oi.sgis.mapper.TimeMapper;
import br.com.oi.sgis.repository.TimeRepository;
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
public class TimeService
{
    private final TimeRepository timeRepository;
    private static final TimeMapper timeMapper = TimeMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<TimeDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = TimeMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if (term.isBlank())
            return PageableUtil.paginate(timeRepository.findAll(paging).map(timeMapper::toDTO),sortMap);

        return PageableUtil.paginate(timeRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(timeMapper::toDTO), sortMap);

    }

    public TimeDTO findById(TimeDTO timeDTO) throws TimeNotFoundException {
        Time timeToVerify = timeMapper.toModel(timeDTO);
        Time time = verifyIfExists(timeToVerify.getId());
        return timeMapper.toDTO(time);
    }

    private Time verifyIfExists(TimeID id) throws TimeNotFoundException {
        return timeRepository.findById(id.getUnityModel().getId(), id.getIntervention().getId())
                .orElseThrow(()-> new TimeNotFoundException(MessageUtils.TIME_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createTime(TimeDTO timeDTO) {
        Time time = timeMapper.toModel(timeDTO);
        Optional<Time> existTime = timeRepository.findById(time.getId());
        if(existTime.isPresent())
            throw new IllegalArgumentException(MessageUtils.TIME_ALREADY_EXISTS.getDescription());
        try {
            timeRepository.save(time);
            return createMessageResponse( MessageUtils.TIME_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TIME_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message ).title("Sucesso!").status(status).build();
    }

    public MessageResponseDTO updateTime(TimeDTO timeDTO) throws TimeNotFoundException {
        Time time = timeMapper.toModel(timeDTO);
        verifyIfExists(time.getId());
        try {
            timeRepository.save(time);
            return createMessageResponse(MessageUtils.TIME_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TIME_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] timeReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<TimeDTO> timeDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(timeDTOS== null || timeDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Tempos Padrão");
        parameters.put("column1", "CÓDIGO");
        parameters.put("column2", "TEMPO");

        List<GenericReportDTO> genericReport = timeDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getUnityModel().getId())
                        .data2(r.getCentesimalTime() != null ? r.getCentesimalTime().toString() : "").build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(TimeDTO timeDTO) throws TimeNotFoundException {
        Time time = timeMapper.toModel(timeDTO);
        verifyIfExists(time.getId());
        try{
            timeRepository.deleteById(time.getId());
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TIME_DELETE_ERROR.getDescription());
        }
    }
}
