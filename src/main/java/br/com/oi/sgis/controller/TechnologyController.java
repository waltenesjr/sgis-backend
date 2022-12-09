package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.dto.TechnologyDTO;
import br.com.oi.sgis.exception.TechnologyNotFoundException;
import br.com.oi.sgis.service.TechnologyService;
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
@RequestMapping("/api/v1/technologies")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class TechnologyController {

    private final TechnologyService technologyService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<TechnologyDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                                                               @RequestParam(required = false) List<String> sortAsc,
                                                                               @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                               @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<TechnologyDTO> allTechnologies =  technologyService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allTechnologies, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public TechnologyDTO findById(@RequestParam String id) throws TechnologyNotFoundException {
        return technologyService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createTechnology(@Valid @RequestBody TechnologyDTO technologyDTO){
        return technologyService.createTechnology(technologyDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateTechnology(@Valid @RequestBody TechnologyDTO technologyDTO) throws TechnologyNotFoundException {
        return technologyService.updateTechnology(technologyDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = technologyService.technologyReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Tecnologias.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String id) throws TechnologyNotFoundException {
        technologyService.deleteById(id);
    }
}
