package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import br.com.oi.sgis.exception.SituationNotFoundException;
import br.com.oi.sgis.service.SituationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/situations")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class SituationController {

    private final SituationService situationService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SituationDTO> listAll(){
        return situationService.listAll();
    }

    @GetMapping("/id")
    @ResponseStatus(HttpStatus.OK)
    public SituationDTO findById(@RequestParam(value = "id") String id) throws SituationNotFoundException {
        return situationService.findById(id);
    }

    @GetMapping("/crp")
    public ResponseEntity<List<SituationDTO>> listAllCRP(){
        List<SituationDTO> situationsCRP =situationService.listAllCRP();
        return new ResponseEntity<>(situationsCRP, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/cus")
    @ResponseStatus(HttpStatus.OK)
    public SituationDTO selectSituationCus(@RequestParam(value = "idResponsible") String id) {
        return situationService.selectSituationCUS(id);
    }

    @GetMapping("/unity-change")
    public ResponseEntity<List<SituationDTO>> listAllSituationsForUpdateUnity(){
        List<SituationDTO> situations =situationService.listAllToUpdateUnity();
        return new ResponseEntity<>(situations, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/write-off")
    @ResponseStatus(HttpStatus.OK)
    public SituationDTO situationsByUnityWriteOffReason(@RequestParam(value = "reason") ReasonForWriteOffEnum reason){
        return situationService.situationsByUnityWriteOffReason(reason);
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<SituationDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                @RequestParam(required = false) List<String> sortAsc,
                                                                                @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<SituationDTO> allTechnologies =  situationService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allTechnologies, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/conciliate")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO conciliate(@Valid @RequestBody SituationDTO situationDTO) throws SituationNotFoundException {
        return situationService.conciliate(situationDTO);
    }
}
