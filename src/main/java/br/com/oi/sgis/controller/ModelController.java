package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.ModelDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.exception.ModelNotFoundException;
import br.com.oi.sgis.service.ModelService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/models")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")public class ModelController {

    private final ModelService modelService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>> listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                                        @RequestParam(required = false) List<String> sortAsc,
                                                                        @RequestParam(required = false, defaultValue = "modelCod") List<String> sortDesc,
                                                                        @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<Object> allModels = modelService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc, term);
        return new ResponseEntity<>(allModels, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public ModelDTO findById(@RequestParam Long modelCod, @RequestParam String manufacturerCod ) throws ModelNotFoundException {
        ModelDTO dto = ModelDTO.builder().modelCod(modelCod).manufacturerCod(manufacturerCod).build();
        return modelService.findById(dto);
    }
}
