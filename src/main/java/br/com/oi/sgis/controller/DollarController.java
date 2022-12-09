package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DollarDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportDollarCrudDTO;
import br.com.oi.sgis.exception.DollarNotFoundException;
import br.com.oi.sgis.service.DollarService;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dollars")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class DollarController {
    private final DollarService dollarService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<DollarDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                                         @RequestParam(required = false) List<String> sortAsc,
                                                                         @RequestParam(required = false, defaultValue = "date") List<String> sortDesc,
                                                                         @RequestParam(required = false) BigDecimal value,
                                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date ) {

        PaginateResponseDTO<DollarDTO> allDollars =  dollarService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,value, date);
        return new ResponseEntity<>(allDollars, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public DollarDTO findById(@RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDateTime id) throws DollarNotFoundException {
        return dollarService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createDollar(@Valid @RequestBody DollarDTO dollarDTO){
        return dollarService.createDollar(dollarDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateDollar(@Valid @RequestBody DollarDTO dollarDTO) throws DollarNotFoundException {
        return dollarService.updateDollar(dollarDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportDollarCrudDTO reportDto) throws JRException, IOException {
        byte[] report = dollarService.dollarReport(reportDto.getValue(), reportDto.getDate(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Dolares.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDateTime id) throws DollarNotFoundException {
        dollarService.deleteById(id);
    }
}
