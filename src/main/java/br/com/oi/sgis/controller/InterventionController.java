package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.InterventionDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.exception.InterventionNotFoundException;
import br.com.oi.sgis.service.InterventionService;
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
@RequestMapping("/api/v1/interventions")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class InterventionController {

    private final InterventionService interventionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<InterventionDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                                         @RequestParam(required = false) List<String> sortAsc,
                                                                         @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                         @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<InterventionDTO> allInterventions =  interventionService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allInterventions, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public InterventionDTO findById(@PathVariable String id) throws InterventionNotFoundException {
        return interventionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createIntervention(@Valid @RequestBody InterventionDTO interventionDTO){
        return interventionService.createIntervention(interventionDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateIntervention(@Valid @RequestBody InterventionDTO interventionDTO) throws InterventionNotFoundException {
        return interventionService.updateIntervention(interventionDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestParam(required = false, defaultValue = "") String search, @RequestParam(required = false, defaultValue = "id") List<String> sortAsc,
                                         @RequestParam(required = false) List<String> sortDesc) throws JRException, IOException {
        byte[] report = interventionService.interventionReport(search, sortAsc, sortDesc);
        String filename ="Relatorio_Intervenções.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) throws InterventionNotFoundException {
        interventionService.deleteById(id);
    }
}
