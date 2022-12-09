package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.GenericQueryTypeDTO;
import br.com.oi.sgis.service.GenericQueryTypeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/type-queries")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class GenericQueryTypeController {

    private GenericQueryTypeService genericQueryTypeService;

    @GetMapping
    public ResponseEntity<List<GenericQueryTypeDTO>> getAll(){
        return new ResponseEntity<>(genericQueryTypeService.findAll(), new HttpHeaders(), HttpStatus.OK);
    }
}
