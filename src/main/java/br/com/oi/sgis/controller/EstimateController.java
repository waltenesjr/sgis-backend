package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.EstimateNotFoundException;
import br.com.oi.sgis.service.EstimateService;
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
@RequestMapping("/api/v1/estimates")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class EstimateController {


    private final EstimateService estimateService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<EstimateDTO>> listOpenWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                  @RequestParam(required = false) List<String> sortAsc,
                                                                                  @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                  @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<EstimateDTO> allEstimates =  estimateService.listAllOpenPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allEstimates, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public EstimateDTO findById(@PathVariable String id) throws EstimateNotFoundException {
        return estimateService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createEstimate(@Valid @RequestBody EstimateDTO estimateDTO){
        return estimateService.createEstimate(estimateDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO updateEstimate(@Valid @RequestBody EstimateDTO estimateDTO) throws EstimateNotFoundException {
        return estimateService.updateEstimate(estimateDTO);
    }


    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestParam(required = false, defaultValue = "") String search, @RequestParam(required = false, defaultValue = "id") List<String> sortAsc,
                                         @RequestParam(required = false) List<String> sortDesc) throws JRException, IOException {
        byte[] report = estimateService.estimateReport(search, sortAsc, sortDesc);
        String filename ="Relatorio_Orcamentos.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @PutMapping("/external-output")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO externalOutput(@Valid @RequestBody EstimateExternalOutputDTO estimateExternalOutputDTO) throws EstimateNotFoundException {
        return estimateService.externalOutput(estimateExternalOutputDTO);
    }

    @GetMapping("/sent")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<EstimateDTO>> listAllSentWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                                                              @RequestParam(required = false) List<String> sortAsc,
                                                                              @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                              @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<EstimateDTO> allEstimates =  estimateService.listAllSentPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allEstimates, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/sent/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> estimateSentReport(@RequestParam(required = false, defaultValue = "") String search, @RequestParam(required = false, defaultValue = "id") List<String> sortAsc,
                                         @RequestParam(required = false) List<String> sortDesc) throws JRException, IOException {
        byte[] report = estimateService.estimateSentReport(search, sortAsc, sortDesc);
        String filename ="Relatorio_Orcamentos.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @PutMapping("/sent")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO updateSentEstimate(@Valid @RequestBody EstimateDTO estimateDTO) throws EstimateNotFoundException {
        return estimateService.updateSentEstimate(estimateDTO);
    }

    @PutMapping("/cancel")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO cancelEstimate(@RequestParam String estimateId) throws EstimateNotFoundException {
        return estimateService.cancelEstimate(estimateId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<EstimateDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                                                              @RequestParam(required = false) List<String> sortAsc,
                                                                              @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                              @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<EstimateDTO> allEstimates =  estimateService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allEstimates, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/return-repair")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO returnRepair(@Valid @RequestBody RepairExternalReturnDTO repairExternalReturnDTO){
        return estimateService.returnRepair(repairExternalReturnDTO);
    }
}
