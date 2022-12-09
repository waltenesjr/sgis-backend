package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DollarDTO;
import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Dollar;
import br.com.oi.sgis.exception.DollarNotFoundException;
import br.com.oi.sgis.mapper.DollarMapper;
import br.com.oi.sgis.repository.DollarRepository;
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DollarService {
    private final DollarRepository dollarRepository;
    private static final DollarMapper dollarMapper = DollarMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<DollarDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, BigDecimal value, LocalDateTime date) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (value != null)
            return PageableUtil.paginate(dollarRepository.findDollarByValueEquals(value, paging).map(dollarMapper::toDTO));
        if(date != null) {
            return PageableUtil.paginate(dollarRepository.findDollarByDateEquals(onlyDate(date), paging).map(dollarMapper::toDTO));
        }
        return PageableUtil.paginate(dollarRepository.findAll(paging).map(dollarMapper::toDTO));
    }

    public DollarDTO findById(LocalDateTime id) throws DollarNotFoundException {
        Dollar dollar = verifyIfExists(onlyDate(id));
        return dollarMapper.toDTO(dollar);
    }

    private LocalDateTime onlyDate(LocalDateTime dateTime){
        return dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private Dollar verifyIfExists(LocalDateTime id) throws DollarNotFoundException {
        return dollarRepository.findById(id)
                .orElseThrow(()-> new DollarNotFoundException(MessageUtils.DOLLAR_NOT_FOUND_BY_ID.getDescription() + getDateFormat(id)));
    }

    public MessageResponseDTO createDollar(DollarDTO dollarDTO) {
        Optional<Dollar> existDollar = dollarRepository.findById(onlyDate(dollarDTO.getDate()));
        if(existDollar.isPresent())
            throw new IllegalArgumentException(MessageUtils.DOLLAR_ALREADY_REGISTERED.getDescription());
        try {
            Dollar dollar = dollarMapper.toModel(dollarDTO);
            dollarRepository.save(dollar);
            return createMessageResponse(getDateFormat(dollar.getDate()), MessageUtils.DOLLAR_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DOLLAR_SAVE_ERROR.getDescription());
        }
    }

    private String getDateFormat(LocalDateTime date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateDollar(DollarDTO dollarDTO) throws DollarNotFoundException {
        verifyIfExists(onlyDate(dollarDTO.getDate()));
        try {
            Dollar dollar = dollarMapper.toModel(dollarDTO);
            dollarRepository.save(dollar);
            return createMessageResponse(getDateFormat(dollar.getDate()), MessageUtils.DOLLAR_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DOLLAR_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] dollarReport(BigDecimal value, LocalDateTime date, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<DollarDTO> dollarDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, value, date).getData();
        if(dollarDTOS== null || dollarDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de cotação do Dólar");
        parameters.put("column1", "DATA");
        parameters.put("column2", "VALOR DO DÓLAR");

        DecimalFormat decFormat = new java.text.DecimalFormat("#,###,##0.00");
        List<GenericReportDTO> genericReport = dollarDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(getDateFormat(r.getDate())).data2("R$ " + decFormat.format(r.getValue())).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(LocalDateTime id) throws DollarNotFoundException {
        verifyIfExists(onlyDate(id));
        try{
            dollarRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DOLLAR_DELETE_ERROR.getDescription());
        }
    }
}
