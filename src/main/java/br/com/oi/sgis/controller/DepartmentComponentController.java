package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DepartmentComponentDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.service.DepartmentComponentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/department-component")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class DepartmentComponentController {

    private final DepartmentComponentService departmentComponentService;

    @GetMapping
    public ResponseEntity<PaginateResponseDTO<DepartmentComponentDTO>> listByDeparmentUserPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                            @RequestParam(required = false) List<String> sortAsc,
                                                                                            @RequestParam(required = false,  defaultValue = "department.id") List<String> sortDesc){

    return new ResponseEntity<>(departmentComponentService.listByDeparmentUserPaginated(pageNo, pageSize, sortAsc, sortDesc),
            new HttpHeaders(), HttpStatus.OK);
    }
}
