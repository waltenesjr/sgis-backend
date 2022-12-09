package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.RepSituationNotFoundException;
import br.com.oi.sgis.exception.TicketInterventionNotFoundException;
import br.com.oi.sgis.service.TicketInterventionService;
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
@RequestMapping("/api/v1/ticket-interventions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TicketInterventionController {

    private final TicketInterventionService ticketInterventionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<TicketInterventionDTO>> listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                                        @RequestParam(required = false) List<String> sortAsc,
                                                                        @RequestParam(required = false, defaultValue = "sequence")  List<String> sortDesc ,
                                                                        @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<TicketInterventionDTO> allTicketInterventions =  ticketInterventionService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allTicketInterventions, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO openIntervention(@Valid @RequestBody TicketInterventionDTO ticketInterventionDTO) {
        return ticketInterventionService.openIntervention(ticketInterventionDTO);
    }

    @GetMapping("/id")
    public TicketInterventionDTO findById(@RequestParam  Long sequence, @RequestParam  String brNumber) throws TicketInterventionNotFoundException {
        TicketInterventionDTO dto = TicketInterventionDTO.builder().repairTicket(RepairTicketDTO.builder().brNumber(brNumber).build())
                .sequence(sequence).build();
        return ticketInterventionService.findByIdDTO(dto);
    }

    @GetMapping("/update-intervention/situation")
    @ResponseStatus(HttpStatus.OK)
    public List<SituationDTO> updateInterventionSituation(@RequestParam String brNumber) throws RepSituationNotFoundException {
        return ticketInterventionService.updateInterventionSituation(brNumber);
    }

    @GetMapping("/technician/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> technicianReport(@RequestParam(required = false)  String idTechnician) throws JRException, IOException {
        byte[] report = ticketInterventionService.technicianReport(idTechnician);
        String filename ="Relatorio_Resumo_Tecnico.pdf";
        return getByteResponse(report, filename);
    }

    @GetMapping("/order-service/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> orderServiceReport(@RequestParam(required = false)  String idTechnician, @RequestParam(required = false)  String barcode) throws JRException, IOException {
        byte[] report = ticketInterventionService.orderServiceReport(idTechnician, barcode);
        String filename ="Relatorio_Ordem_Servico.pdf";
        return getByteResponse(report, filename);
    }

    private ResponseEntity<byte[]> getByteResponse(byte[] report, String filename) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @PutMapping("/update-intervention")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateIntervention(@Valid @RequestBody TicketInterventionUpdateDTO ticketInterventionUpdateDTO) {
        return ticketInterventionService.updateIntervention(ticketInterventionUpdateDTO);
    }

    @PutMapping("/close-intervention")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO closeIntervention(@Valid @RequestBody TicketInterventionUpdateDTO ticketInterventionUpdateDTO) {
        return ticketInterventionService.closeIntervention(ticketInterventionUpdateDTO);
    }

    @GetMapping("/close")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<TicketInterventionDTO>> listAllToClosePaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                       @RequestParam(required = false) List<String> sortAsc,
                                                                                       @RequestParam(required = false, defaultValue = "sequence")  List<String> sortDesc ,
                                                                                       @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<TicketInterventionDTO> allTicketInterventions =  ticketInterventionService.listAllToClosePaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allTicketInterventions, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<TicketInterventionDTO>> listAllToUpdatePaginated(@RequestParam String brNumber,
                                                                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                                                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                              @RequestParam(required = false) List<String> sortAsc,
                                                                                              @RequestParam(required = false, defaultValue = "sequence")  List<String> sortDesc ,
                                                                                              @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<TicketInterventionDTO> allTicketInterventions =  ticketInterventionService.listAllToUpdatePaginated(pageNo, pageSize, sortAsc, sortDesc,term, brNumber);
        return new ResponseEntity<>(allTicketInterventions, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/technician-times")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> technicianTimesReport(@RequestBody TechnicianTimesFilterDTO filterDTO)  {
        byte[] report = ticketInterventionService.technicianTimesReport(filterDTO);
        String filename ="Relatorio_Tempos_Tecnico.pdf";
        return getByteResponse(report, filename);
    }
}
