package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.ModelTechnicianNotFoundException;
import br.com.oi.sgis.service.ModelTechnicianService;
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
@RequestMapping("/api/v1/technician-models")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class ModelTechnicianController {
    private final ModelTechnicianService modelTechnicianService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<ModelTechnicianDTO>> listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                    @RequestParam(required = false) List<String> sortAsc,
                                                                                    @RequestParam(required = false,  defaultValue = "department.id") List<String> sortDesc,
                                                                                    @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<ModelTechnicianDTO> allTechnicianModels =  modelTechnicianService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allTechnicianModels, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public ModelTechnicianDTO findById(@RequestParam  String departmentId, @RequestParam  String modelUnityId, @RequestParam String technicianId) throws ModelTechnicianNotFoundException {
        ModelTechnicianDTO dto = ModelTechnicianDTO.builder().department(DepartmentDTO.builder().id(departmentId).build())
                .model(AreaEquipamentDTO.builder().id(modelUnityId).build()).technicalStaff(TechnicalStaffDTO.builder().id(technicianId).build()).build();
        return modelTechnicianService.findById(dto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createModelTechnician(@Valid @RequestBody ModelTechnicianDTO modelTechnicianDTO){
        return modelTechnicianService.createModelTechnician(modelTechnicianDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateModelTechnician(@Valid @RequestBody ModelTechnicianDTO modelTechnicianDTO) throws ModelTechnicianNotFoundException {
        return modelTechnicianService.updateModelTechnician(modelTechnicianDTO);
    }


    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = modelTechnicianService.modelTechnicianReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Tecnicos_Modelo.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam  String departmentId, @RequestParam  String modelUnityId, @RequestParam String technicianId) throws ModelTechnicianNotFoundException {
        ModelTechnicianDTO dto = ModelTechnicianDTO.builder().department(DepartmentDTO.builder().id(departmentId).build())
                .model(AreaEquipamentDTO.builder().id(modelUnityId).build()).technicalStaff(TechnicalStaffDTO.builder().id(technicianId).build()).build();
        modelTechnicianService.deleteById(dto);
    }
}
