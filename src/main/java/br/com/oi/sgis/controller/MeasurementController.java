package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MeasurementDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.MeasurementNotFoundException;
import br.com.oi.sgis.service.MeasurementService;
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
@RequestMapping("/api/v1/measurements")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MeasurementController {
    private final MeasurementService measurementService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MeasurementDTO> listAll(){
        return measurementService.listAll();
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<MeasurementDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                                                            @RequestParam(required = false) List<String> sortAsc,
                                                                            @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                            @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<MeasurementDTO> allMeasurements =  measurementService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allMeasurements, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public MeasurementDTO findById(@PathVariable String id) throws MeasurementNotFoundException {
        return measurementService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createMeasurement(@Valid @RequestBody MeasurementDTO measurementDTO){
        return measurementService.createMeasurement(measurementDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateMeasurement(@Valid @RequestBody MeasurementDTO measurementDTO) throws MeasurementNotFoundException {
        return measurementService.updateMeasurement(measurementDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = measurementService.measurementReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Un_Medidas.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) throws MeasurementNotFoundException {
        measurementService.deleteById(id);
    }
}
