package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.ManufacturerDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.ManufacturerNotFoundException;
import br.com.oi.sgis.service.ManufacturerService;
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
@RequestMapping("/api/v1/manufacturers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<ManufacturerDTO>> listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                                        @RequestParam(required = false) List<String> sortAsc,
                                                                        @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                        @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<ManufacturerDTO> allManufacturers = manufacturerService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc, term);
        return new ResponseEntity<>(allManufacturers, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ManufacturerDTO findById(@PathVariable String id) throws ManufacturerNotFoundException {
        return manufacturerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createManufacturer(@Valid @RequestBody ManufacturerDTO manufacturerDTO){
        return manufacturerService.createManufacturer(manufacturerDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateManufacturer(@Valid @RequestBody ManufacturerDTO manufacturerDTO) throws ManufacturerNotFoundException {
        return manufacturerService.updateManufacturer(manufacturerDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = manufacturerService.manufacturerReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Fabricantes.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) throws ManufacturerNotFoundException {
        manufacturerService.deleteById(id);
    }
}
