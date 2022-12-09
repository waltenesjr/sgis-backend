package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.ApplicationDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.ApplicationNotFoundException;
import br.com.oi.sgis.service.ApplicationService;
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
@RequestMapping("/api/v1/applications")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<ApplicationDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                                         @RequestParam(required = false) List<String> sortAsc,
                                                                         @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                         @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<ApplicationDTO> allApplications =  applicationService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allApplications, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ApplicationDTO findById(@PathVariable String id) throws ApplicationNotFoundException {
        return applicationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createApplication(@Valid @RequestBody ApplicationDTO applicationDTO){
        return applicationService.createApplication(applicationDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateApplication(@Valid @RequestBody ApplicationDTO applicationDTO) throws ApplicationNotFoundException {
        return applicationService.updateApplication(applicationDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = applicationService.applicationReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Aplicacoes.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) throws ApplicationNotFoundException {
        applicationService.deleteById(id);
    }
}
