package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.ItemEstimateNotFoundException;
import br.com.oi.sgis.service.ItemEstimateService;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/item-estimates")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class ItemEstimateController {

    private final ItemEstimateService itemEstimateService;

    @PostMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<ItemEstimateDTO>> listAllWithSearch(@RequestBody ItemEstimatesAnalysisDTO itemDTO, @RequestParam(defaultValue = "1") Integer pageNo,
                                                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                  @RequestParam(required = false) List<String> sortAsc,
                                                                                  @RequestParam(required = false, defaultValue = "id") List<String> sortDesc) {

        PaginateResponseDTO<ItemEstimateDTO> allItemEstimates =  itemEstimateService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,itemDTO);
        return new ResponseEntity<>(allItemEstimates, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/analysis")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateAnalysis(@RequestBody List<ItemEstimateDTO> items) throws ItemEstimateNotFoundException {
        return itemEstimateService.updateAnalysis(items);
    }

    @PutMapping("/approve-cancel")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateCancelApprove(@RequestBody List<ApproveCancelItemEstimateDTO> items) throws ItemEstimateNotFoundException {
        return itemEstimateService.updateCancelApprove(items);
    }

    @PostMapping("/analysis/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> analysisReport(@RequestBody ItemEstimatesAnalysisDTO itemDTO,
                                         @RequestParam(required = false) List<String> sortAsc,
                                         @RequestParam(required = false, defaultValue = "id") List<String> sortDesc) throws JRException, IOException {
        byte[] report = itemEstimateService.analysisReport(itemDTO, sortAsc, sortDesc);
        String filename ="Relatorio_Analise_Item_Orcamento.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }
}
