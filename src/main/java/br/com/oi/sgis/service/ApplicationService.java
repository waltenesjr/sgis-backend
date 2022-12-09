package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ApplicationDTO;
import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Application;
import br.com.oi.sgis.exception.ApplicationNotFoundException;
import br.com.oi.sgis.mapper.ApplicationMapper;
import br.com.oi.sgis.repository.ApplicationRepository;
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
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private static final ApplicationMapper applicationMapper = ApplicationMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<ApplicationDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(applicationRepository.findAll(paging).map(applicationMapper::toDTO));

        return PageableUtil.paginate(applicationRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(applicationMapper::toDTO));

    }

    public ApplicationDTO findById(String id) throws ApplicationNotFoundException {
        Application application = verifyIfExists(id);
        return applicationMapper.toDTO(application);
    }

    private Application verifyIfExists(String id) throws ApplicationNotFoundException {
        return applicationRepository.findById(id)
                .orElseThrow(()-> new ApplicationNotFoundException(MessageUtils.APPLICATION_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createApplication(ApplicationDTO applicationDTO) {
        Optional<Application> existApplication = applicationRepository.findById(applicationDTO.getId());
        if(existApplication.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        try {
            Application application = applicationMapper.toModel(applicationDTO);
            applicationRepository.save(application);
            return createMessageResponse(application.getId(), MessageUtils.APPLICATION_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.APPLICATION_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateApplication(ApplicationDTO applicationDTO) throws ApplicationNotFoundException {
        verifyIfExists(applicationDTO.getId());
        try {
            Application application = applicationMapper.toModel(applicationDTO);
            applicationRepository.save(application);
            return createMessageResponse(application.getId(), MessageUtils.APPLICATION_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.APPLICATION_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] applicationReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<ApplicationDTO> applicationDTOS  =  listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(applicationDTOS== null || applicationDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Aplicação de equipamentos");
        parameters.put("column1", "APLICAÇÃO");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = applicationDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(String id) throws ApplicationNotFoundException {
        verifyIfExists(id);
        try{
            applicationRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.APPLICATION_DELETE_ERROR.getDescription());
        }
    }
}
