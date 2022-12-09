package br.com.oi.sgis.controller;

import br.com.oi.sgis.entity.Uf;
import br.com.oi.sgis.service.UfService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/ufs")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class UfController {

    private final UfService ufService;

    @GetMapping
    public ResponseEntity<List<Uf>> listAll(){
        List<Uf> allUfs =   ufService.listAll();
        return new ResponseEntity<>(allUfs, new HttpHeaders(), HttpStatus.OK);
    }
}
