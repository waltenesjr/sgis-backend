package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.*;
import br.com.oi.sgis.service.*;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/reports")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")public class ReportController {

    private final MovItensService movItensService;
    private final SummaryItemService summaryItemService;
    private final ItemBySituationService itemBySituationService;
    private final StockSummaryService stockSummaryService;
    private final ItensInstallByStealReasonService itensInstallByStealReasonService;
    private final GeneralItensService generalItensService;
    private final RegisteredItensService registeredItensService;

    @PostMapping("/movement-report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> movementReport(@Valid @RequestBody MovItensReportDTO motItensReportDTO,  @RequestParam TypeDocEnum typeDocEnum) throws JRException {
        byte[] movementReport = movItensService.report(motItensReportDTO, typeDocEnum);
        String filename ="Relatorio de Movimentos".concat(typeDocEnum.getType());
        return getBodyResponse(movementReport, filename);
    }

    private ResponseEntity<byte[]> getBodyResponse(byte[] report, String filename) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @GetMapping("/movement-report/order-list")
    public List<ReporterOrderDTO> orderByList(){
        return Arrays.stream(MovItensReportOrderEnum.values()).map(
                itens->
                ReporterOrderDTO.builder().cod(itens.getOrderBy()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @GetMapping("/summary-item/group-list")
    public List<ReporterOrderDTO> groupList(){
        return Arrays.stream(ItensSumaryReportBreakEnum.values()).map(
                        itens->
                                ReporterOrderDTO.builder().cod(itens.getOrderBy()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PostMapping("/summary-item")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> summaryItemReport(@Valid @RequestBody SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO, @RequestParam TypeDocEnum typeDocEnum) {
        byte[] summaryReport = summaryItemService.report(summaryItemCriteriaReportDTO, typeDocEnum);
        String filename ="Relatorio_Sumario_Itens".concat(typeDocEnum.getType());
        return getBodyResponse(summaryReport, filename);
    }

    @GetMapping("/situation-item/report-type")
    public List<ReporterOrderDTO> typeList(){
        return Arrays.stream(ItemBySitReportTypeEnum.values()).map(
                        itens->
                                ReporterOrderDTO.builder().cod(itens.getCod()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PostMapping("/situation-item")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> situationItemReport(@Valid @RequestBody ItemBySitReportCriteriaDTO itemBySitCriteriaReportDTO,  @RequestParam TypeDocEnum typeDocEnum) {
        byte[] itemBySitReport = itemBySituationService.report(itemBySitCriteriaReportDTO, typeDocEnum);
        String filename ="Relatorio_Itens_Situação".concat(typeDocEnum.getType());
        return getBodyResponse(itemBySitReport, filename);
    }

    @GetMapping("/stock-summary/filter-type")
    public List<FilteringDTO> filterList(){
        return Arrays.stream(FilteringEnum.values()).map(
                        itens->
                                FilteringDTO.builder().filter(itens.getFilter()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PostMapping("/stock-summary")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> stockSummaryReport(@Valid @RequestBody StockSummaryCriteriaDTO stockSummaryCriteriaDTO, @RequestParam TypeDocEnum typeDocEnum) {
        byte[] stockSummaryReport = stockSummaryService.report(stockSummaryCriteriaDTO, typeDocEnum);
        String filename ="Relatorio_Sumario_Estoque".concat(typeDocEnum.getType());
        return getBodyResponse(stockSummaryReport, filename);
    }

    @GetMapping("/install-steals/reasons")
    public List<InstallationReasonDTO> reasonsSteal(){
        return InstallationReasonEnum.steals().stream().map(
                        reason->
                                InstallationReasonDTO.builder().cod(reason.getCod()).description(reason.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PostMapping("/install-steals")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> installStealsReport(@Valid @RequestBody ItensInstallByStealReasonCriteriaDTO itensInstallByStealReasonCriteriaDTO,  @RequestParam TypeDocEnum typeDocEnum) {
        byte[] stockSummaryReport = itensInstallByStealReasonService.report(itensInstallByStealReasonCriteriaDTO, typeDocEnum);
        String filename ="Relatorio_Itens_Instalados_Furto".concat(typeDocEnum.getType());
        return getBodyResponse(stockSummaryReport, filename);
    }

    @GetMapping("/general-itens/order")
    public List<ReporterOrderDTO> generalOrderList(){
        return Arrays.stream(GeneralItensReportBreakEnum.values()).map(
                        itens->
                                ReporterOrderDTO.builder().cod(itens.getOrderBy()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PostMapping("/general-itens")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> generalItens(@Valid @RequestBody GeneralItensCriteriaDTO generalItensCriteriaDTO, @RequestParam TypeDocEnum typeDocEnum){
        byte[] stockSummaryReport = generalItensService.report(generalItensCriteriaDTO, typeDocEnum);
        String filename ="Relatorio_Geral_Itens".concat(typeDocEnum.getType());
        return getBodyResponse(stockSummaryReport, filename);
    }

    @PostMapping("/registered-itens")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> registeredItens(@Valid @RequestBody RegisteredItensCriteriaDTO registeredItensCriteriaDTO, @RequestParam TypeDocEnum typeDocEnum){
        byte[] stockSummaryReport = registeredItensService.report(registeredItensCriteriaDTO, typeDocEnum);
        String filename ="Relatorio_Itens_Cadastrados".concat(typeDocEnum.getType());
        return getBodyResponse(stockSummaryReport, filename);
    }

    @GetMapping("/type")
    public List<ReporterOrderDTO> typeDoc(){
        return Arrays.stream(TypeDocEnum.values()).map(
                        itens->
                                ReporterOrderDTO.builder().cod(itens.getCod()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }
}
