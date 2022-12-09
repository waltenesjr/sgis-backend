package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.CompanyDTO;
import br.com.oi.sgis.dto.FiscalDocumentDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.FiscalDocument;
import br.com.oi.sgis.entity.FiscalDocumentId;
import br.com.oi.sgis.exception.FiscalDocumentNotFoundException;
import br.com.oi.sgis.mapper.FiscalDocumentMapper;
import br.com.oi.sgis.repository.FiscalDocumentRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FiscalDocumentService {

    private final FiscalDocumentRepository fiscalDocumentRepository;
    private static final FiscalDocumentMapper fiscalDocumentMapper = FiscalDocumentMapper.INSTANCE;
    private final ContractService contractService;
    private final CompanyService companyService;

    public List<FiscalDocumentDTO> listAll(){
        List<FiscalDocument> allFiscalDocuments = fiscalDocumentRepository.findAll();
        return allFiscalDocuments.stream().map(fiscalDocumentMapper::toDTO).collect(Collectors.toList());
    }

    public PaginateResponseDTO<Object> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term, LocalDateTime initialDate, LocalDateTime finalDate) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = FiscalDocumentMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if(initialDate!=null && finalDate!=null)
            return listAllPaginatedByDate(paging, term, initialDate, finalDate);
        if(term.isBlank())
            return PageableUtil.paginate(fiscalDocumentRepository.findAll(paging).map(fiscalDocumentMapper::toDTO),sortMap);

        return PageableUtil.paginate( fiscalDocumentRepository
                .findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(fiscalDocumentMapper::toDTO), sortMap);
    }
    private PaginateResponseDTO<Object> listAllPaginatedByDate(Pageable paging, String term, LocalDateTime initialDate, LocalDateTime finalDate) {
       if(initialDate.isAfter(finalDate))
           throw new IllegalArgumentException(MessageUtils.INVALID_PERIOD.getDescription());

        if(term.isBlank())
            return PageableUtil.paginate(fiscalDocumentRepository
                    .findAllByDateBetween(initialDate, finalDate, paging).map(fiscalDocumentMapper::toDTO), FiscalDocumentMapper.getMappedValues());

        return PageableUtil.paginate( fiscalDocumentRepository
                .findLikeDateBetween(term.toUpperCase(Locale.ROOT).trim(), initialDate, finalDate, paging)
                .map(fiscalDocumentMapper::toDTO), FiscalDocumentMapper.getMappedValues());
    }

    public FiscalDocumentDTO findById(FiscalDocumentId id) throws FiscalDocumentNotFoundException{
        FiscalDocument fiscalDocument = verifyIfExists(id);
        return fiscalDocumentMapper.toDTO(fiscalDocument);
    }

    @SneakyThrows
    public FiscalDocumentDTO findByIdDto(FiscalDocumentDTO dto) throws FiscalDocumentNotFoundException{
        FiscalDocument model = fiscalDocumentMapper.toModel(dto);

        FiscalDocument fiscalDocument = verifyIfExists(model.getId());

        FiscalDocumentDTO fiscalDocumentDTO =  fiscalDocumentMapper.toDTO(fiscalDocument);
        CompanyDTO companyDTO = companyService.findById(fiscalDocumentDTO.getCompanyId());
        fiscalDocumentDTO.setCompanyName(companyDTO.getCompanyName());
        return fiscalDocumentDTO;
    }

    private FiscalDocument verifyIfExists(FiscalDocumentId id) throws FiscalDocumentNotFoundException {
        return fiscalDocumentRepository.findById(id)
                .orElseThrow(()-> new FiscalDocumentNotFoundException(MessageUtils.FISCAL_DOCUMENT_NOT_FOUND_BY_ID.getDescription()));
    }

    public MessageResponseDTO createDocument(FiscalDocumentDTO fiscalDocumentDTO) {
        FiscalDocument fiscalDocument = fiscalDocumentMapper.toModel(fiscalDocumentDTO);
        Optional<FiscalDocument> existFiscalDocument = fiscalDocumentRepository.findById(fiscalDocument.getId());
        if(existFiscalDocument.isPresent())
            throw new IllegalArgumentException(MessageUtils.DOCUMENT_ALREADY_REGISTERED.getDescription());
        validateDocument(fiscalDocument);
        try {
            fiscalDocumentRepository.save(fiscalDocument);
            return createMessageResponse(MessageUtils.DOCUMENT_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DOCUMENT_SAVE_ERROR.getDescription());
        }
    }

    public MessageResponseDTO updateDocument(FiscalDocumentDTO fiscalDocumentDTO) throws FiscalDocumentNotFoundException {
        FiscalDocument fiscalDocument = fiscalDocumentMapper.toModel(fiscalDocumentDTO);
        verifyIfExists(fiscalDocument.getId());
        validateDocument(fiscalDocument);
        try {
            fiscalDocumentRepository.save(fiscalDocument);
            return createMessageResponse(MessageUtils.DOCUMENT_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DOCUMENT_UPDATE_ERROR.getDescription());
        }
    }

    @SneakyThrows
    private void validateDocument(FiscalDocument fiscalDocument) {
        if(fiscalDocument.getContract() != null)
            contractService.findById(fiscalDocument.getContract().getId());
        companyService.findById(fiscalDocument.getId().getCgcCPf().getId());
    }

    private MessageResponseDTO createMessageResponse(String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message).status(status).build();
    }

    public void deleteByDto(FiscalDocumentDTO dto) throws FiscalDocumentNotFoundException {
        FiscalDocument fiscalDocument = fiscalDocumentMapper.toModel(dto);
        verifyIfExists(fiscalDocument.getId());
        try{
            fiscalDocumentRepository.deleteById(fiscalDocument.getId());
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.DOCUMENT_DELETE_ERROR.getDescription());
        }
    }
}
