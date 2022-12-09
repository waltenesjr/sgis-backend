package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.GenericQuery;
import br.com.oi.sgis.entity.GenericQueryItem;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.exception.GenericQueryNotFoundException;
import br.com.oi.sgis.mapper.GenericQueryMapper;
import br.com.oi.sgis.repository.GenericQueryRepository;
import br.com.oi.sgis.service.factory.GenericQueryExecutionFactory;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static br.com.oi.sgis.util.MessageUtils.*;
import static java.util.stream.Collectors.toList;


@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GenericQueryService {

    private final GenericQueryRepository genericQueryRepository;
    private final GenericQueryItemService queryItemService;
    private static final GenericQueryMapper genericQueryMapper = GenericQueryMapper.INSTANCE;
    private final GenericQueryExecutionFactory executionQueryFactory;
    private final ReportService reportService;

    public PaginateResponseDTO<GenericQueryDTO> findAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc,
                                                                 List<String> sortDesc, String search){

        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = GenericQueryMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));

        if (search.isBlank())
            return PageableUtil.paginate(genericQueryRepository.findAll(paging).map(genericQueryMapper::toDTO));

        return PageableUtil.paginate(genericQueryRepository.findLike(paging, search.toUpperCase(Locale.ROOT)).map(genericQueryMapper::toDTO));
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO create(GenericQueryDTO dto) {
        createValidId(dto);
        GenericQuery entity = genericQueryMapper.toModel(dto);
        entity.setGenericQueryItems(null);
        try {
            genericQueryRepository.save(entity);
            saveGenericQueryItem(dto);
            return createMessageResponse(entity.getId(), GENERIC_QUERY_CREATE_SUCCESS.getDescription(), HttpStatus.CREATED);
        } catch (Exception e){
            throw new IllegalArgumentException(GENERIC_QUERY_CREATE_ERROR.getDescription());
        }
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO update(GenericQueryDTO dto) throws GenericQueryNotFoundException {
        GenericQuery entity = verifyExists(dto.getId());
        if (!entity.getTechnicalStaff().getId().equals(Utils.getUser().getId()))
            throw new IllegalArgumentException(GENERIC_QUERY_UPDATE_DIFFERENT_USER_ERROR.getDescription());
        GenericQuery updateGenericQuery = genericQueryMapper.toModel(dto);
        updateGenericQueryItem(dto);
        try {
            genericQueryRepository.save(updateGenericQuery);
            return createMessageResponse(entity.getId(), GENERIC_QUERY_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        } catch (Exception e){
            throw new IllegalArgumentException(GENERIC_QUERY_UPDATE_ERROR.getDescription());
        }
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public void delete(Long id) throws GenericQueryNotFoundException {
        GenericQuery entity = verifyExists(id);
        if (!entity.getTechnicalStaff().getId().equals(Utils.getUser().getId()))
            throw new IllegalArgumentException(GENERIC_QUERY_DELETE_DIFFERENT_USER_ERROR.getDescription());
        try {
            entity.getGenericQueryItems().forEach(queryItemService::delete);
            genericQueryRepository.delete(entity);
        } catch (Exception e){
            throw new IllegalArgumentException(GENERIC_QUERY_DELETE_ERROR.getDescription());
        }
    }

    private void saveGenericQueryItem(GenericQueryDTO dto){
        dto.getColumns().forEach(c -> {
            c.setGenericQueryId(dto.getId());
            queryItemService.save(c);
        });
    }

    private void updateGenericQueryItem(GenericQueryDTO dto) throws GenericQueryNotFoundException {
        List<GenericQueryItem> oldQueryItems = verifyExists(dto.getId()).getGenericQueryItems();
        List<GenericQueryItemDTO> newQueryItems = dto.getColumns();

        if (oldQueryItems.isEmpty() && newQueryItems.isEmpty())
            return;
        if (oldQueryItems.isEmpty()){
            saveGenericQueryItem(dto);
        } else if (newQueryItems.isEmpty()) {
            oldQueryItems.forEach(queryItemService::delete);
        } else {
            oldQueryItems.forEach(queryItemService::delete);
            saveGenericQueryItem(dto);
        }
    }

    private void createValidId(GenericQueryDTO dto) {
        GenericQuery topQuery = genericQueryRepository.findTopByOrderByIdDesc();
        Long lastId = topQuery.getId();
        dto.setId(lastId + 1);
    }

    private GenericQuery verifyExists(Long id) throws GenericQueryNotFoundException {
        return genericQueryRepository.findById(id)
                .orElseThrow(() -> new GenericQueryNotFoundException(GENERIC_QUERY_NOT_FOUND_BY_ID.getDescription() + id));
    }

    private MessageResponseDTO createMessageResponse(Long id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public byte[] executeQuery(GenericQueryDTO dto, TypeDocEnum typeDocEnum) throws JRException, IOException {
        String query;
        try {
            query = executionQueryFactory.createSqlQuery(dto);
        }catch (RuntimeException  e){
            throw new IllegalArgumentException(MessageUtils.ERROR_GENERIC_QUERY_REPORT.getDescription());
        }
        List<Object> results = genericQueryRepository.executeQuery(query);
        if(results.isEmpty())
            throw new IllegalArgumentException(EMPTY_REPORT.getDescription());
        List<String> columnsNames = dto.getColumns().stream().filter(c-> !c.isNoShow()).sorted(Comparator.comparing(GenericQueryItemDTO::getColumnSequence)).map(GenericQueryItemDTO::getColumnName).collect(toList());
        if(dto.isTotalizeFlag())
            columnsNames.add("Quantidade");
        List<List<String>> resultsString = getResultString(results);

        List<GenericQueryResultDTO> genericQueryResultDTOS = new ArrayList<>();
        genericQueryResultDTOS.add(GenericQueryResultDTO.builder().result(resultsString).columns(columnsNames).build());

        try {
            return reportService.genericQuery(genericQueryResultDTOS, typeDocEnum);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }

    }

    private List<List<String>> getResultString(List<Object> results) {
        List<List<String>> resultsString = new ArrayList<>();
        for (Object o : results) {
            List<String> attributes = new ArrayList<>();
            Object[] result = ((Object[]) o);
            for (int j = 0; j < result.length; j++) {
                String attr = ((Object[]) o)[j] != null ? getString(((Object[]) o)[j]) : "";
                attributes.add(attr);
            }
            resultsString.add(attributes);
        }
        return resultsString;
    }

    private String getString(Object o) {
        if(o.getClass() == Date.class) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return  ((Date) o).toLocalDate().format(formatter);
        }
        return o.toString();
    }

    public GenericQueryDTO findById(Long id) throws GenericQueryNotFoundException {
        return genericQueryMapper.toDTO(verifyExists(id));
    }
}
