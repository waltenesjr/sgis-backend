package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DefectDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.DefectNotFoundException;
import br.com.oi.sgis.service.DefectService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/defects")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DefectController {

    private final DefectService defectService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DefectDTO> listAll(){
        return defectService.listAll();
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<DefectDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                                         @RequestParam(required = false) List<String> sortAsc,
                                                                         @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                         @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<DefectDTO> allDefects =  defectService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allDefects, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public DefectDTO findById(@PathVariable String id) throws DefectNotFoundException {
        return defectService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createDefect(@Valid @RequestBody DefectDTO defectDTO){
        return defectService.createDefect(defectDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateDefect(@Valid @RequestBody DefectDTO defectDTO) throws DefectNotFoundException {
        return defectService.updateDefect(defectDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = defectService.defectReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Defeitos.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) throws DefectNotFoundException {
        defectService.deleteById(id);
    }

}
