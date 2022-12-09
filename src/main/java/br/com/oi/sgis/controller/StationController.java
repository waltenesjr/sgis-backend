package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.dto.StationDTO;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.service.StationService;
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
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class StationController {

    private final StationService stationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<StationDTO>>  listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                                         @RequestParam(required = false) List<String> sortAsc,
                                                                         @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                         @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<StationDTO> stationDtoPage =  stationService.listPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(stationDtoPage, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public StationDTO findById(@RequestParam String id) throws StationNotFoundException {
        return stationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createStation(@Valid @RequestBody StationDTO stationDTO){
        return stationService.createStation(stationDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateStation(@Valid @RequestBody StationDTO stationDTO) throws StationNotFoundException {
        return stationService.updateStation(stationDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(   @RequestBody ReportCrudSearchDTO reportDto)  throws JRException, IOException {
        byte[] report = stationService.stationReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Estacoes.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String id) throws StationNotFoundException {
        stationService.deleteById(id);
    }
}
