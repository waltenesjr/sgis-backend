package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.PriorityRepairEnum;
import br.com.oi.sgis.enums.SituationSapEnum;
import br.com.oi.sgis.service.InformaticsRecService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/informatics-rec")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class InformaticsRecController {

    private final InformaticsRecService informaticsRecService;

    @PostMapping("/sap-operation-history")
    public ResponseEntity<PaginateResponseDTO<SapOperationHistoryReportDTO>> getSapOperationHistory (@RequestBody SapOperationHistoryDTO dto,
                                                                                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                                                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                                     @RequestParam(required = false) List<String> sortAsc,
                                                                                                     @RequestParam(required = false,defaultValue = "id") List<String> sortDesc) throws Exception {
        PaginateResponseDTO<SapOperationHistoryReportDTO> allSapOperationHistory =
                informaticsRecService.getSapOperationHistory(dto, pageNo, pageSize, sortAsc, sortDesc);

        return new ResponseEntity<>(allSapOperationHistory, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/situation-sap")
    @ResponseStatus(HttpStatus.OK)
    public List<SituationSapDTO> situations() {
        return Arrays.stream(SituationSapEnum.values()).map(
                        situation->
                                SituationSapDTO.builder().cod(situation.getCod()).description(situation.getDescription()).build())
                .collect(Collectors.toList());
    }
}
