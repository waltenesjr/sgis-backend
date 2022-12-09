package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.TimeNotFoundException;
import br.com.oi.sgis.service.TimeService;
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
@RequestMapping("/api/v1/times")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class TimeController {
    private final TimeService timeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<TimeDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                @RequestParam(required = false) List<String> sortAsc,
                                                                                @RequestParam(required = false, defaultValue = "unityModel.id") List<String> sortDesc,
                                                                                @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<TimeDTO> allTimes =  timeService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allTimes, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public TimeDTO findById(@RequestParam String interventionId, @RequestParam String unityModelId ) throws TimeNotFoundException {
        TimeDTO timeDTO = TimeDTO.builder().intervention(InterventionDTO.builder().id(interventionId).build())
                .unityModel(AreaEquipamentDTO.builder().id(unityModelId).build()).build();
        return timeService.findById(timeDTO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createTime(@Valid @RequestBody TimeDTO timeDTO){
        return timeService.createTime(timeDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateTime(@Valid @RequestBody TimeDTO timeDTO) throws TimeNotFoundException {
        return timeService.updateTime(timeDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = timeService.timeReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Tempo_Padrao.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String interventionId, @RequestParam String unityModelId ) throws TimeNotFoundException {
        TimeDTO timeDTO = TimeDTO.builder().intervention(InterventionDTO.builder().id(interventionId).build())
                .unityModel(AreaEquipamentDTO.builder().id(unityModelId).build()).build();
        timeService.deleteById(timeDTO);
    }
}
