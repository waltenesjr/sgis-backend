package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<DepartmentDTO>>  listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                                         @RequestParam(required = false) List<String> sortAsc,
                                                                         @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                         @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<DepartmentDTO> departmentDtoPage =  departmentService.listPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(departmentDtoPage, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/repair-centers")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<DepartmentDTO>>  listAllRepairCenters(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                @RequestParam(required = false) List<String> sortAsc,
                                                                                @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<DepartmentDTO> departmentDtoPage =  departmentService.listAllRepairCenter(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(departmentDtoPage, new HttpHeaders(), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public DepartmentDTO findById(@PathVariable String id) throws DepartmentNotFoundException {
        return departmentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO){
        return departmentService.createDepartment(departmentDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateDepartment(@Valid @RequestBody DepartmentDTO departmentDTO){
        return departmentService.updateDepartment(departmentDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = departmentService.departmentReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Areas_Administrativas.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @GetMapping("/devolution-department/{id}")
    public DepartmentDTO devolutionDepartment(@PathVariable String id) {
        return departmentService.devolutionDepartmentByUnity(id);
    }

    @GetMapping("/users-extraction")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<DepartmentDTO>>  listAllForUsersExtraction(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                @RequestParam(required = false) List<String> sortAsc,
                                                                                @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<DepartmentDTO> departmentDtoPage =  departmentService.listAllForUsersExtraction(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(departmentDtoPage, new HttpHeaders(), HttpStatus.OK);
    }
}
