package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.ComponentNotFoundException;
import br.com.oi.sgis.service.ComponentService;
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
@RequestMapping("/api/v1/components")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class ComponentController {

    private final ComponentService componentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<ComponentDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                                         @RequestParam(required = false) List<String> sortAsc,
                                                                         @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                         @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<ComponentDTO> allComponents =  componentService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allComponents, new HttpHeaders(), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ComponentDTO findById(@PathVariable String id) throws ComponentNotFoundException {
        return componentService.findById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createComponent(@Valid @RequestBody ComponentDTO componentDTO) throws ComponentNotFoundException {
        return componentService.createComponent(componentDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateComponent(@Valid @RequestBody ComponentDTO componentDTO) throws ComponentNotFoundException {
        return componentService.updateComponent(componentDTO);
    }


    @GetMapping("/type")
    @ResponseStatus(HttpStatus.OK)
    public List<ComponentTypeDTO> componentTypeList(){
        return componentService.componentTypes();
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = componentService.componentReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Componentes.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) throws ComponentNotFoundException {
        componentService.deleteById(id);
    }

}
