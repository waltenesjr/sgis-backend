package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.GenericQueryDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReporterOrderDTO;
import br.com.oi.sgis.enums.MovItensReportOrderEnum;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.exception.GenericQueryNotFoundException;
import br.com.oi.sgis.service.GenericQueryService;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/generic-query")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class GenericQueryController {

    private final GenericQueryService genericQueryService;

    @GetMapping
    public ResponseEntity<PaginateResponseDTO<GenericQueryDTO>> listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                 @RequestParam(required = false) List<String> sortAsc,
                                                                                 @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                 @RequestParam(defaultValue = "", required = false) String search) {

        PaginateResponseDTO<GenericQueryDTO> allGenericQueries =
                genericQueryService.findAllPaginated(pageNo, pageSize, sortAsc, sortDesc, search);
        return new ResponseEntity<>(allGenericQueries, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GenericQueryDTO getById(@PathVariable Long id) throws GenericQueryNotFoundException {
        return genericQueryService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createGenericQuery(@RequestBody GenericQueryDTO dto) {
        return genericQueryService.create(dto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateGenericQuery(@RequestBody GenericQueryDTO dto) throws GenericQueryNotFoundException {
        return genericQueryService.update(dto);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam Long id) throws GenericQueryNotFoundException {
        genericQueryService.delete(id);
    }

    @PostMapping("/execute")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<byte[]> executeGenericQuery(@RequestBody GenericQueryDTO dto, @RequestParam TypeDocEnum typeDocEnum) throws JRException, IOException {
        byte[] report = genericQueryService.executeQuery(dto, typeDocEnum);
        String filename ="Consulta".concat(typeDocEnum.getType());
        return  ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(report);
    }

    @GetMapping("/execute/type")
    public List<ReporterOrderDTO> typeDoc(){
        return TypeDocEnum.txtXlsx().stream().map(
                        itens->
                                ReporterOrderDTO.builder().cod(itens.getCod()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }
}
