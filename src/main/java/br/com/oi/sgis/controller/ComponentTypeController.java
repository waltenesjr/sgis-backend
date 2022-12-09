package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.ComponentTypeDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.ComponentTypeNotFoundException;
import br.com.oi.sgis.service.ComponentTypeService;
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
@RequestMapping("/api/v1/component-types")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class ComponentTypeController {
    private final ComponentTypeService componentTypeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<ComponentTypeDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                                                             @RequestParam(required = false) List<String> sortAsc,
                                                                             @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                             @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<ComponentTypeDTO> allComponentType =  componentTypeService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allComponentType, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public ComponentTypeDTO findById(@RequestParam String id) throws ComponentTypeNotFoundException {
        return componentTypeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createComponentType(@Valid @RequestBody ComponentTypeDTO componentTypeDTO){
        return componentTypeService.createComponentType(componentTypeDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateComponentType(@Valid @RequestBody ComponentTypeDTO componentTypeDTO) throws ComponentTypeNotFoundException {
        return componentTypeService.updateComponentType(componentTypeDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = componentTypeService.componentTypeReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Tipos_Componente.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String id) throws ComponentTypeNotFoundException {
        componentTypeService.deleteById(id);
    }
}
