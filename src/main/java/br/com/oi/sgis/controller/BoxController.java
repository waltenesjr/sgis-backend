package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.BoxNotFoundException;
import br.com.oi.sgis.service.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boxes")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BoxController {

    private final BoxService boxService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>> listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                                        @RequestParam(required = false) List<String> sortAsc,
                                                                        @RequestParam(required = false) List<String> sortDesc,
                                                                        @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<Object> allBoxes =  boxService.listPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allBoxes, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BoxDTO findById(@PathVariable String id){
        return boxService.findById(id);
    }

    @GetMapping("/box-type")
    @ResponseStatus(HttpStatus.OK)
    public List<BoxTypeDTO> listBoxType(){
        return boxService.listBoxType();
    }

    @GetMapping("/box/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BoxToUpdateDTO getBoxToUpdate(@PathVariable String id) throws BoxNotFoundException {
        return boxService.getBoxToUpdate(id);
    }

    @PutMapping("/box/update")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateBox(@RequestBody BoxToUpdateDTO boxToUpdateDTO) throws BoxNotFoundException {
        return boxService.updateBox(boxToUpdateDTO);
    }

    @PostMapping("/summary-report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> summaryBoxReport(@RequestParam String boxId) {
        byte[] report = boxService.summaryBoxReport(boxId);
        String filename ="Relatorio_Resumo_Caixa.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

}
