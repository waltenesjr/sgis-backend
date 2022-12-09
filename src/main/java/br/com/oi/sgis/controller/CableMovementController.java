package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.CableMovementNotFoundException;
import br.com.oi.sgis.service.CableMovementService;
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
@RequestMapping("/api/v1/movement-cables")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class CableMovementController {

    private final CableMovementService cableMovementService;

    @PostMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<CableMovementDTO>> listAll(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                                         @RequestParam(required = false) List<String> sortAsc,
                                                                         @RequestParam(required = false,defaultValue = "unity.id") List<String> sortDesc,
                                                                         @Valid @RequestBody CableMovementFilterDTO filterDTO) {

        PaginateResponseDTO<CableMovementDTO> allCableMovements =  cableMovementService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,filterDTO);
        return new ResponseEntity<>(allCableMovements, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public CableMovementDTO findById(@RequestParam String unityId, @RequestParam Long sequence) throws CableMovementNotFoundException {
        return cableMovementService.findById(sequence, unityId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createCableMovement(@Valid @RequestBody CableMovementDTO cableMovementDTO){
        return cableMovementService.createCableMovement(cableMovementDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody CableMovementFilterDTO filterDTO, @RequestParam (required = false) List<String> sortAsc,
                                         @RequestParam (required = false, defaultValue = "id") List<String> sortDesc) throws JRException, IOException {
        byte[] report = cableMovementService.cableMovementReport(filterDTO, sortAsc, sortDesc);
        String filename ="Relatorio_Mov_Cabos.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @GetMapping("/properties/{barcode}")
    public List<ElectricalPropDTO> electricalProp(@PathVariable("barcode") String barcode) {
        return cableMovementService.listCableMovementUnityProperties(barcode);
    }

    @PostMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public PaginateResponseDTO<CableMovementDTO> getCableMovement(@RequestBody CableMovementQueryDTO cableMovementQueryDTO,
                                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                                              @RequestParam (required = false) List<String> sortAsc,
                                                              @RequestParam (required = false, defaultValue = "id") List<String> sortDesc) {
        return cableMovementService.getCableMovement(cableMovementQueryDTO,  pageNo, pageSize, sortAsc, sortDesc);
    }

    @PostMapping("/query/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> reportQuery(@RequestBody CableMovementQueryDTO cableMovementQueryDTO, @RequestParam (required = false) List<String> sortAsc,
                                         @RequestParam (required = false, defaultValue = "id") List<String> sortDesc) throws JRException, IOException {
        byte[] report = cableMovementService.cableMovementQueryReport(cableMovementQueryDTO , sortAsc, sortDesc);
        String filename ="Relatorio_Consulta_Mov_Cabos.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }
}
