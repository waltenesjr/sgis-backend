package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DomainDTO;
import br.com.oi.sgis.service.DomainService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/domain")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class DomainController {

    private final DomainService domainService;

    @GetMapping("accountant-company")
    @ResponseStatus(HttpStatus.OK)
    public List<DomainDTO> listAccountantCompany(){
        return domainService.getAccountCompanyDomain();
    }
}
