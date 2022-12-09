package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.CurrencyTypeDTO;
import br.com.oi.sgis.dto.FiscalDocumentDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.enums.CurrencyTypeEnum;
import br.com.oi.sgis.exception.FiscalDocumentNotFoundException;
import br.com.oi.sgis.service.FiscalDocumentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/documents")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class FiscalDocumentController {

    private final FiscalDocumentService fiscalDocumentService;

    @GetMapping("/list/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<FiscalDocumentDTO>> listAll(){
        List<FiscalDocumentDTO> fiscalDocumentDTOList = fiscalDocumentService.listAll();
        return new ResponseEntity<>(fiscalDocumentDTOList,  new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>>  listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                                     LocalDateTime initialDate,
                                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                                     LocalDateTime finalDate,
                                                                          @RequestParam(required = false) List<String> sortAsc,
                                                                          @RequestParam(required = false,  defaultValue = "docNumber") List<String> sortDesc,
                                                                          @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<Object> allDocuments =  fiscalDocumentService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term, initialDate, finalDate);
        return new ResponseEntity<>(allDocuments, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/fiscalDocument")
    public FiscalDocumentDTO findById(@RequestParam Long docNumber, @RequestParam String companyId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime docDate ) throws FiscalDocumentNotFoundException {
        FiscalDocumentDTO dto = FiscalDocumentDTO.builder().docNumber(docNumber).companyId(companyId).docDate(docDate).build();
        return fiscalDocumentService.findByIdDto(dto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createFiscalDocument(@Valid @RequestBody FiscalDocumentDTO fiscalDocumentDTO){
        return fiscalDocumentService.createDocument(fiscalDocumentDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateFiscalDocument(@Valid @RequestBody FiscalDocumentDTO fiscalDocumentDTO) throws FiscalDocumentNotFoundException {
        return fiscalDocumentService.updateDocument(fiscalDocumentDTO);
    }

    @DeleteMapping("/fiscalDocument")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam Long docNumber, @RequestParam String companyId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime docDate ) throws FiscalDocumentNotFoundException {
        FiscalDocumentDTO dto = FiscalDocumentDTO.builder().docNumber(docNumber).companyId(companyId).docDate(docDate).build();
        fiscalDocumentService.deleteByDto(dto);
    }

    @GetMapping("/currencyType")
    public List<CurrencyTypeDTO> currencyType(){
        return Arrays.stream(CurrencyTypeEnum.values()).map(c-> CurrencyTypeDTO.builder().id(c.getId()).description(c.getDescription()).build())
                .collect(Collectors.toList());
    }
}
