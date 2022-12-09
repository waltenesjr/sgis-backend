package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.AreaEquipamentDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.enums.ActiveClassEnum;
import br.com.oi.sgis.exception.AreaEquipamentNotFoundException;
import br.com.oi.sgis.service.AreaEquipamentService;
import lombok.AllArgsConstructor;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/equipaments")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class AreaEquipamentController {

    private final AreaEquipamentService areaEquipamentService;


    @GetMapping("/id")
    @ResponseStatus(HttpStatus.OK)
    public AreaEquipamentDTO findById(@RequestParam(value = "id") String id) throws AreaEquipamentNotFoundException {
        return areaEquipamentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createAreaEquipament(@Valid @RequestBody AreaEquipamentDTO areaEquipamentDTO){
        return areaEquipamentService.createAreaEquipament(areaEquipamentDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateAreaEquipament(@Valid @RequestBody AreaEquipamentDTO areaEquipamentDTO) throws AreaEquipamentNotFoundException {
        return areaEquipamentService.updateAreaEquipament(areaEquipamentDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<AreaEquipamentDTO>>  listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                      @RequestParam(required = false) List<String> sortAsc,
                                                                                      @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                      @RequestParam(defaultValue = "", required = false, value = "search") String term,
                                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date ) {

        PaginateResponseDTO<AreaEquipamentDTO> allEquipaments =  areaEquipamentService.searchByTermsPaginated(pageNo, pageSize, sortAsc, sortDesc,term, date);
        return new ResponseEntity<>(allEquipaments, new HttpHeaders(), HttpStatus.OK);
    }
    @GetMapping ("/active-class")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ActiveClassEnum>  getActiveClass(@RequestParam(value = "idUnityCode") String idUnityCode) throws AreaEquipamentNotFoundException {
        ActiveClassEnum activeClass = areaEquipamentService.getActiveClass(idUnityCode);
        return new ResponseEntity<>(activeClass, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/mnemonics")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>>  listAllMnemonicsWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                  @RequestParam(required = false) List<String> sortAsc,
                                                                                  @RequestParam(required = false, defaultValue = "mnemonic") List<String> sortDesc,
                                                                                  @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<Object> allMnemonics =  areaEquipamentService.searchMnemonics(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allMnemonics, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String id) throws AreaEquipamentNotFoundException {
        areaEquipamentService.deleteById(id);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date ) throws JRException, IOException {
        byte[] report = areaEquipamentService.areaEquipamentReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc(), date);
        String filename ="Relatorio_Modelos_Unidades.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }
}
