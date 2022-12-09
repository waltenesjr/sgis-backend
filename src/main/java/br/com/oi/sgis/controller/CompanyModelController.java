package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.CompanyModelNotFoundException;
import br.com.oi.sgis.service.CompanyModelService;
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
@RequestMapping("/api/v1/company-models")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class CompanyModelController {

    private final CompanyModelService companyModelService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<CompanyModelDTO>> listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                    @RequestParam(required = false) List<String> sortAsc,
                                                                                    @RequestParam(required = false,  defaultValue = "department.id") List<String> sortDesc,
                                                                                    @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<CompanyModelDTO> allCompanyModels =  companyModelService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allCompanyModels, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public CompanyModelDTO findById(@RequestParam  String companyId, @RequestParam  String equipamentId,@RequestParam  String departmentId ) throws CompanyModelNotFoundException {
        CompanyModelDTO dto = CompanyModelDTO.builder().company(CompanyDTO.builder().id(companyId).build())
                .equipament(AreaEquipamentDTO.builder().id(equipamentId).build()).department(DepartmentDTO.builder().id(departmentId).build()).build();
        return companyModelService.findByIdDTO(dto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createCompanyModel(@Valid @RequestBody CompanyModelDTO companyModelDTO){
        return companyModelService.createCompanyModel(companyModelDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateCompanyModel(@Valid @RequestBody CompanyModelDTO companyModelDTO) throws CompanyModelNotFoundException {
        return companyModelService.updateCompanyModel(companyModelDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = companyModelService.companyModelReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Modelos_Por_Empresa.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam  String companyId, @RequestParam  String equipamentId, @RequestParam  String departmentId) throws  CompanyModelNotFoundException {
        CompanyModelDTO dto = CompanyModelDTO.builder().company(CompanyDTO.builder().id(companyId).build())
                .equipament(AreaEquipamentDTO.builder().id(equipamentId).build()).department(DepartmentDTO.builder().id(departmentId).build()).build();
        companyModelService.deleteById(dto);
    }

}
