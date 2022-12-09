package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.BoxTypeDTO;
import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.BoxType;
import br.com.oi.sgis.exception.BoxTypeNotFoundException;
import br.com.oi.sgis.mapper.BoxTypeMapper;
import br.com.oi.sgis.repository.BoxTypeRepository;
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
public class BoxTypeService {
    private final BoxTypeRepository boxTypeRepository;
    private static final BoxTypeMapper boxTypeMapper = BoxTypeMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<BoxTypeDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(boxTypeRepository.findAll(paging).map(boxTypeMapper::toDTO));

        return PageableUtil.paginate(boxTypeRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(boxTypeMapper::toDTO));

    }

    public BoxTypeDTO findById(String id) throws BoxTypeNotFoundException {
        BoxType boxType = verifyIfExists(id);
        return boxTypeMapper.toDTO(boxType);
    }

    private BoxType verifyIfExists(String id) throws BoxTypeNotFoundException {
        return boxTypeRepository.findById(id)
                .orElseThrow(()-> new BoxTypeNotFoundException(MessageUtils.BOX_TYPE_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createBoxType(BoxTypeDTO boxTypeDTO) {
        Optional<BoxType> existBoxType = boxTypeRepository.findById(boxTypeDTO.getId());
        if(existBoxType.isPresent())
            throw new IllegalArgumentException(MessageUtils.BOX_TYPE_ALREADY_EXISTS.getDescription());
        try {
            BoxType boxType = boxTypeMapper.toModel(boxTypeDTO);
            boxTypeRepository.save(boxType);
            return createMessageResponse(boxType.getId(), MessageUtils.BOX_TYPE_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.BOX_TYPE_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateBoxType(BoxTypeDTO boxTypeDTO) throws BoxTypeNotFoundException {
        verifyIfExists(boxTypeDTO.getId());
        try {
            BoxType boxType = boxTypeMapper.toModel(boxTypeDTO);
            boxTypeRepository.save(boxType);
            return createMessageResponse(boxType.getId(), MessageUtils.BOX_TYPE_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.BOX_TYPE_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] boxTypeReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<BoxTypeDTO> boxTypeDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(boxTypeDTOS== null || boxTypeDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Tipo de Caixa");
        parameters.put("column1", "TIPO DE CAIXA");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = boxTypeDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(String id) throws BoxTypeNotFoundException {
        verifyIfExists(id);
        try{
            boxTypeRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.BOX_TYPE_DELETE_ERROR.getDescription());
        }
    }
}
