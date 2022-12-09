package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.TechniqueDTO;
import br.com.oi.sgis.dto.TechniqueTypeDTO;
import br.com.oi.sgis.enums.TechniqueTypeEnum;
import br.com.oi.sgis.exception.TechniqueNotFoundException;
import br.com.oi.sgis.service.TechniqueService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/techniques")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class TechniqueController {
    private final TechniqueService techniqueService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<TechniqueDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                @RequestParam(required = false) List<String> sortAsc,
                                                                                @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<TechniqueDTO> allTechniques =  techniqueService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allTechniques, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public TechniqueDTO findById(@RequestParam String id) throws TechniqueNotFoundException {
        return techniqueService.findById(id);
    }

    @GetMapping("/technique-type")
    public List<TechniqueTypeDTO> techniqueTypes()  {
        return Arrays.stream(TechniqueTypeEnum.values()).map(
                        techType->
                                TechniqueTypeDTO.builder().cod(techType.getCod()).description(techType.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createTechnique(@Valid @RequestBody TechniqueDTO techniqueDTO){
        return techniqueService.createTechnique(techniqueDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateTechnique(@Valid @RequestBody TechniqueDTO techniqueDTO) throws TechniqueNotFoundException {
        return techniqueService.updateTechnique(techniqueDTO);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String id) throws TechniqueNotFoundException {
        techniqueService.deleteById(id);
    }
}
