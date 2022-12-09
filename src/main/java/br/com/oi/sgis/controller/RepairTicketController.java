package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.*;
import br.com.oi.sgis.exception.RepSituationNotFoundException;
import br.com.oi.sgis.exception.RepairTicketException;
import br.com.oi.sgis.service.RepairTicketService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tickets-repair")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RepairTicketController {

    private final RepairTicketService service;

    @GetMapping("/unity/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UnityDTO unity(@PathVariable String id) {
        return service.getUnity(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RepairTicketDTO findById(@PathVariable String id) throws RepairTicketException {
        return service.findById(id);
    }

    @GetMapping("/priorities")
    @ResponseStatus(HttpStatus.OK)
    public List<PriorityRepairDTO> priorities() {
        return Arrays.stream(PriorityRepairEnum.values()).map(
                        priority->
                                PriorityRepairDTO.builder().cod(priority.getCod()).description(priority.getDescription()).build())
                .collect(Collectors.toList());
    }

    @GetMapping("/situation-default")
    @ResponseStatus(HttpStatus.OK)
    public SituationDTO repSituation() throws RepSituationNotFoundException {
        return service.getDefaultRepSituation();
    }

    @GetMapping("/fas-number")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO fasNumber(@RequestParam(value = "fasNumber", required = false) String fasNumber) {
        return service.fasNumber(fasNumber);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createRepairTicket(@Valid @RequestBody RepairTicketDTO repairTicketDTO){
        return service.createRepairTicket(repairTicketDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                                         @RequestParam (required = false) List<String> sortAsc,
                                                                         @RequestParam (required = false, defaultValue = "id") List<String> sortDesc,
                                                                         @RequestParam(defaultValue = "", required = false, value = "search") String term){

        PaginateResponseDTO<Object> repairTickets =  service.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(repairTickets, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateRepairTicket(@Valid @RequestBody RepairTicketDTO repairTicketDTO){
        return service.updateRepairTicket(repairTicketDTO);
    }

    @GetMapping("/{id}/extra-information")
    @ResponseStatus(HttpStatus.OK)
    public TicketRepExtraInfoDTO extraInformation(@PathVariable String id) throws RepairTicketException {
        return service.getExtraInformation(id);
    }

    @GetMapping("/accept-repair/unity/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AcceptTicketRepairDTO acceptUnity(@PathVariable String id) {
        return service.getAcceptRepair(id);
    }

    @PutMapping("/accept-repair")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO acceptUnity(@Valid @RequestBody AcceptTicketRepairDTO acceptTicketRepairDTO) {
        return service.acceptRepair(acceptTicketRepairDTO);
    }

    @GetMapping("/accept-repair/unity/{id}/warranty-date")
    public ResponseEntity<MessageResponseDTO> verifyWarranty(@PathVariable String id) {
        return service.verifyWarrantyDate(id);
    }

    @PostMapping("/forward-repair/filter")
    @ResponseStatus(HttpStatus.OK)
    public PaginateResponseDTO<RepairTicketDTO> forwardRepair(@RequestBody ForwardTicketDTO forwardTicketDTO,
                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam (required = false) List<String> sortAsc,
                                              @RequestParam (required = false, defaultValue = "id") List<String> sortDesc) {
        return service.getForwardRepair(forwardTicketDTO,  pageNo, pageSize, sortAsc, sortDesc);
    }

    @PutMapping("/forward-repair")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO forwardRepair( @RequestBody RepairTicketDTO repairTicketDTO) {
        return service.forwardRepair(repairTicketDTO);
    }

    @GetMapping("/forward-repair/{id}")
    public RepairTicketDTO forwardRepair(@PathVariable String id) throws RepairTicketException {
        return service.ticketToForward(id);
    }

    @PostMapping("/forward-repair/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> forwardTicketReport(@RequestParam (required = false) List<String> sortAsc,
                                         @RequestParam (required = false, defaultValue = "id") List<String> sortDesc, @RequestBody ForwardTicketDTO forwardTicketDTO) throws JRException, IOException {
        byte[] report = service.forwardTicketReport(forwardTicketDTO, sortAsc, sortDesc);
        String filename ="Relatorio_Encaminhamento.pdf";
        return getResponseEntity(report, filename);
    }

    private ResponseEntity<byte[]> getResponseEntity(byte[] report, String filename) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @PostMapping("/close-repair")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO closeRepair(@Valid @RequestBody CloseRepairTickectDTO closeRepairTickectDTO)  {
        return service.closeRepair(closeRepairTickectDTO);
    }

    @GetMapping("/close-repair/situation")
    @ResponseStatus(HttpStatus.OK)
    public SituationDTO closeRepairSituation(@RequestParam String unityId) throws RepSituationNotFoundException {
        return service.closeRepairSituation(unityId);
    }

    @PostMapping("/devolution-repair")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO devolutionRepair(@Valid @RequestBody DevolutionRepairTicketDTO devolutionRepairTicketDTO)  {
        return service.devolutionRepair(devolutionRepairTicketDTO);
    }

    @PostMapping("/cancel-repair")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO cancelRepair(@Valid @RequestParam String unityId)  {
        return service.cancelRepair(unityId);
    }

    @PostMapping("/designate-technician/filter")
    @ResponseStatus(HttpStatus.OK)
    public PaginateResponseDTO<RepairTicketDTO> getDesignateTechnician(@RequestBody DesignateTechnicianDTO designateTechnicianDTO,
                                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                                              @RequestParam (required = false) List<String> sortAsc,
                                                              @RequestParam (required = false, defaultValue = "id") List<String> sortDesc) {
        return service.getDesignateTechnician(designateTechnicianDTO,  pageNo, pageSize, sortAsc, sortDesc);
    }

    @PutMapping("/designate-technician")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO designateTechnician( @RequestBody RepairTicketDTO repairTicketDTO) {
        return service.designateTechnician(repairTicketDTO);
    }

    @GetMapping("/open-intervention")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<RepairTicketDTO>> listAllForOpenIntervention(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                                         @RequestParam (required = false) List<String> sortAsc,
                                                                         @RequestParam (required = false, defaultValue = "id") List<String> sortDesc,
                                                                         @RequestParam(defaultValue = "", required = false, value = "search") String term){

        PaginateResponseDTO<RepairTicketDTO> repairTickets =  service.getTicketsForIntervention(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(repairTickets, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/summary-report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> summaryReport( @RequestBody RepairSummaryFilterDTO repairSummaryFilterDTO) throws JRException, IOException {
        byte[] report = service.repairSummaryReport(repairSummaryFilterDTO);
        String filename ="Relatorio_Sumario.pdf";
        return getResponseEntity(report, filename);
    }

    @PostMapping("/analytic-report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> analyticReport( @RequestBody RepairAnalyticFilterDTO repairSummaryFilterDTO) {
        byte[] report = service.analyticReport(repairSummaryFilterDTO);
        String filename ="Relatorio_Analitico.pdf";
        return getResponseEntity(report, filename);
    }

    @GetMapping("/analytic-report/breaks")
    @ResponseStatus(HttpStatus.OK)
    public List<ReporterOrderDTO> analyticReport(){
        return Arrays.stream(BreaksAnalyticRepairEnum.values()).map(
                        itens->
                                ReporterOrderDTO.builder().cod(itens.getBreaks()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PostMapping("/external-report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> externalRepairReport( @RequestBody ExternalRepairFilterDTO externalRepairFilterDTO) {
        byte[] report = service.externalRepairReport(externalRepairFilterDTO);
        String filename ="Relatorio_Reparos_Externos.pdf";
        return getResponseEntity(report, filename);
    }

    @PostMapping("/operator-report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> operatorTicketReport( @RequestBody OperatorTicketFilterDTO operatorTicketFilterDTO) {
        byte[] report = service.operatorTicketReport(operatorTicketFilterDTO);
        String filename ="Relatorio_Bilhetes_Operdora.pdf";
        return getResponseEntity(report, filename);
    }

    @PostMapping("/released-report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> ticketReleasedForReturnReport( @RequestBody TicketReleasedFilterDTO ticketReleasedFilterDTO) {
        byte[] report = service.ticketReleasedForReturnReport(ticketReleasedFilterDTO);
        String filename ="Relatorio_Liberado_Devolucao.pdf";
        return getResponseEntity(report, filename);
    }

    @GetMapping("/released-report/conditions")
    @ResponseStatus(HttpStatus.OK)
    public List<ReporterOrderDTO> conditionsReleasedReport(){
        return Arrays.stream(ConditionsTicketReleasedEnum.values()).map(
                        itens->
                                ReporterOrderDTO.builder().cod(itens.getCondition()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PostMapping("/forwarded-report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> ticketForwardedReport( @RequestBody TicketForwardedFilterDTO ticketForwardedFilterDTO) {
        byte[] report = service.ticketForwardedReport(ticketForwardedFilterDTO);
        String filename ="Relatorio_Encaminhados_Reparo.pdf";
        return getResponseEntity(report, filename);
    }

    @GetMapping("/forwarded-report/conditions")
    @ResponseStatus(HttpStatus.OK)
    public List<ReporterOrderDTO> conditionsForwardedReport(){
        return Arrays.stream(ConditionsTicketForwardedEnum.values()).map(
                        itens->
                                ReporterOrderDTO.builder().cod(itens.getCondition()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PostMapping("/average-time/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> averageTimeReport( @RequestBody AverageTimeFilterDTO averageTimeFilterDTO) {
        byte[] report = service.averageTimeReport(averageTimeFilterDTO);
        String filename ="Relatorio_Tempo_Medio_Reparo.pdf";
        return getResponseEntity(report, filename);
    }

    @GetMapping("/summary-equipment/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> summaryEquipmentReport( @RequestParam(value = "repairCenter", required = false) String repairCenter,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime initialDate,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime finalDate) {
        byte[] report = service.summaryEquipmentReport(repairCenter, initialDate, finalDate);
        String filename ="Relatorio_Sumario_Tipo_Eqpto.pdf";
        return getResponseEntity(report, filename);
    }

    @PostMapping("/open/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> openRepairReport(@RequestBody OpenRepairFilterDTO openRepairFilterDTO ) {
        byte[] report = service.openRepairReport(openRepairFilterDTO);
        String filename ="Relatorio_Acomp_Reparo_Aberto.pdf";
        return getResponseEntity(report, filename);
    }

    @PostMapping("/cost-comparison/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> costComparisonRepairReport(@RequestBody CostComparisonRepairFilterDTO costComparisonRepairFilterDTO ) throws JRException, IOException {
        byte[] report = service.costComparisonRepairReport(costComparisonRepairFilterDTO);
        String filename ="Relatorio_Comparativo_Custo.pdf";
        return getResponseEntity(report, filename);
    }

    @GetMapping("/cost-comparison/details")
    @ResponseStatus(HttpStatus.OK)
    public List<ReporterOrderDTO> costComparisonRepairReportDetails(){
        return Arrays.stream(CostComparisonReportBreakEnum.values()).map(
                        itens->
                                ReporterOrderDTO.builder().cod(itens.getOrderBy()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PostMapping("/productivity-comparison")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> productivityComparisonReport(@RequestBody @Valid ProductivityComparisonFilterDTO dto){
        byte[] report = service.productivityComparisonReport(dto);
        String filename = "Relatorio_Comparativo_Produtividade.pdf";
        return getResponseEntity(report, filename);
    }

}
