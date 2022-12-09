package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.DepartmentUnityNotFoundException;
import br.com.oi.sgis.service.DepartmentUnityService;
import lombok.AllArgsConstructor;
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
@RequestMapping("/api/v1/department-unities")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class DepartmentUnityController {

    private final DepartmentUnityService departmentUnityService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<DepartmentUnityDTO>> listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                                   @RequestParam(required = false) List<String> sortAsc,
                                                                        @RequestParam(required = false,  defaultValue = "department.id") List<String> sortDesc,
                                                                        @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<DepartmentUnityDTO> allDepartmentUnities =  departmentUnityService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allDepartmentUnities, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public DepartmentUnityDTO findById(@RequestParam  String departmentId, @RequestParam  String modelUnityId) throws DepartmentUnityNotFoundException {
        DepartmentUnityDTO dto = DepartmentUnityDTO.builder().department(DepartmentDTO.builder().id(departmentId).build())
                .modelUnity(AreaEquipamentDTO.builder().id(modelUnityId).build()).build();
        return departmentUnityService.findByIdDTO(dto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createDepartmentUnity(@Valid @RequestBody DepartmentUnityDTO departmentUnityDTO){
        return departmentUnityService.createDepartmentUnity(departmentUnityDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateDepartmentUnity(@Valid @RequestBody DepartmentUnityDTO departmentUnityDTO) throws DepartmentUnityNotFoundException {
        return departmentUnityService.updateDepartmentUnity(departmentUnityDTO);
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createDepartmentUnityAdmin(@Valid @RequestBody DepartmentUnityDTO departmentUnityDTO){
        return departmentUnityService.createDepartmentUnityAdmin(departmentUnityDTO);
    }

    @PutMapping("/admin")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateDepartmentUnityAdmin(@Valid @RequestBody DepartmentUnityDTO departmentUnityDTO) throws DepartmentUnityNotFoundException {
        return departmentUnityService.updateDepartmentUnityAdmin(departmentUnityDTO);
    }

    @PutMapping("/admin/unity-location")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateUnityLocation(@Valid @RequestBody DepartmentUnityDTO departmentUnityDTO) throws DepartmentUnityNotFoundException {
        return departmentUnityService.updateLocationUnities(departmentUnityDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = departmentUnityService.departmentUnityReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Localizacao_Armazenamento.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam  String departmentId, @RequestParam  String modelUnityId) throws DepartmentUnityNotFoundException {
        DepartmentUnityDTO dto = DepartmentUnityDTO.builder().department(DepartmentDTO.builder().id(departmentId).build())
                .modelUnity(AreaEquipamentDTO.builder().id(modelUnityId).build()).build();
        departmentUnityService.deleteById(dto);
    }
}
