package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.UnityHistoricalViewDTO;
import br.com.oi.sgis.dto.UnityHistoricalViewFilterDTO;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.service.UnityHistoricalViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/unity-historical")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class UnityHistoricalViewController {

    private final UnityHistoricalViewService unityHistoricalViewService;

    @PostMapping
    public ResponseEntity<PaginateResponseDTO<UnityHistoricalViewDTO>> findPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                     @RequestParam(required = false) List<String> sortAsc,
                                                                                     @RequestParam(required = false, defaultValue = "LOG_DATA") List<String> sortDesc,
                                                                                     @RequestBody UnityHistoricalViewFilterDTO dto){

        PaginateResponseDTO<UnityHistoricalViewDTO> unityHistorical =
                unityHistoricalViewService.findPaginated(dto, pageNo, pageSize, sortAsc, sortDesc);
        return new ResponseEntity<>(unityHistorical, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/report")
    public ResponseEntity<byte[]> unityHistoricalReport(@RequestParam(required = false) List<String> sortAsc,
                                        @RequestParam(required = false, defaultValue = "LOG_DATA") List<String> sortDesc,
                                        @RequestBody UnityHistoricalViewFilterDTO dto, TypeDocEnum typeDocEnum){
        byte[] report = unityHistoricalViewService.unityHistoricalReport(dto, sortAsc, sortDesc, typeDocEnum);
        String filename ="Consulta_Log_Unidade".concat(typeDocEnum.getType());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(report);
    }
}
