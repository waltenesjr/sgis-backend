package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.CentralDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.CentralNotFoundException;
import br.com.oi.sgis.service.CentralService;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/centrals")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class CentralController {

    private final CentralService centralService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<CentralDTO>> listAll(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                                                              @RequestParam(required = false) List<String> sortAsc,
                                                                              @RequestParam(required = false,defaultValue = "id") List<String> sortDesc,
                                                                              @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<CentralDTO> allCentrals =  centralService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allCentrals, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public CentralDTO findById(@PathVariable String id) throws CentralNotFoundException {
        return centralService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createCentral(@Valid @RequestBody CentralDTO centralDTO){
        return centralService.createCentral(centralDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateCentral(@Valid @RequestBody CentralDTO centralDTO) throws CentralNotFoundException {
        return centralService.updateCentral(centralDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = centralService.centralReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Agrupamentos.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) throws CentralNotFoundException {
        centralService.deleteById(id);
    }

}
