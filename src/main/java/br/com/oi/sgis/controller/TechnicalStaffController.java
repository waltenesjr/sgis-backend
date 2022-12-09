package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.TechnicalStaffNotFoundException;
import br.com.oi.sgis.service.TechnicalStaffService;
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
@RequestMapping("/api/v1/technicals")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class TechnicalStaffController {

    private final TechnicalStaffService technicalStaffService;

    @GetMapping("/{id}")
    public TechnicalStaffDTO findById(@PathVariable String id) throws TechnicalStaffNotFoundException {
        return technicalStaffService.findById(id);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<TechnicalStaffDTO>> listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                                        @RequestParam(required = false) List<String> sortAsc,
                                                                        @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                        @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<TechnicalStaffDTO> technicianDtoPage =  technicalStaffService.listPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(technicianDtoPage, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createTechnicalStaff(@Valid @RequestBody TechnicalStaffDTO technicalStaffDTO){
        return technicalStaffService.createTechnicalStaff(technicalStaffDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateTechnicalStaff(@Valid @RequestBody TechnicalStaffDTO technicalStaffDTO) throws TechnicalStaffNotFoundException {
        return technicalStaffService.updateTechnicalStaff(technicalStaffDTO);
    }

    @PutMapping("/man-hour")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateManHour(@RequestBody TechnicalStaffDTO technicalStaffDTO) throws TechnicalStaffNotFoundException {
        return technicalStaffService.updateManHour(technicalStaffDTO);
    }


    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = technicalStaffService.technicalStaffReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Tecnicos.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String id) throws TechnicalStaffNotFoundException {
        technicalStaffService.deleteById(id);
    }

    @GetMapping(value = {"/forward-ticket", "/repair-ticket"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<TechnicalStaffDTO>> listAllToForwardTicket(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                   @RequestParam(required = false) List<String> sortAsc,
                                                                                   @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                   @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<TechnicalStaffDTO> technicianDtoPage =  technicalStaffService.listAllToForwardTicket(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(technicianDtoPage, new HttpHeaders(), HttpStatus.OK);
    }


    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO transferTechnicalStaff(@Valid @RequestBody TransferTechnicalDTO transferTechnicalDTO) throws TechnicalStaffNotFoundException {
        return technicalStaffService.transferTechnicalStaff(transferTechnicalDTO);
    }

    @PostMapping("/emit-proof")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> emitProof(@RequestBody EmitProofDTO emitProofDTO) throws TechnicalStaffNotFoundException, JRException, IOException {
        byte[] report = technicalStaffService.emitProof(emitProofDTO);
        String filename ="Comprovante_Tecnico.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @GetMapping("/local")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<TechnicalStaffDTO>> listAllByUnity(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                  @RequestParam(required = false) List<String> sortAsc,
                                                                                  @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                  @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<TechnicalStaffDTO> technicianDtoPage =  technicalStaffService.listAllByUnity(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(technicianDtoPage, new HttpHeaders(), HttpStatus.OK);
    }
}
