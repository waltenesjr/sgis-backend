package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.service.RepSituationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repair-situations")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RepSituationController {

    private final RepSituationService repSituationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SituationDTO> listAll(){
        return repSituationService.listAll();
    }

    @GetMapping("/forward-repair")
    @ResponseStatus(HttpStatus.OK)
    public List<SituationDTO> listForwardRepair(){
        return repSituationService.listForwardRepair();
    }

    @GetMapping("/intervention/forward-repair")
    @ResponseStatus(HttpStatus.OK)
    public List<SituationDTO> listForwardRepairFromInterv(){
        return repSituationService.listForwardRepairFromInterv();
    }
}
