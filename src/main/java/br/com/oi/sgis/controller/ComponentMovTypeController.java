package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.MovTypeEnum;
import br.com.oi.sgis.enums.SignalMovTypeEnum;
import br.com.oi.sgis.exception.ComponentMovTypeNotFoundException;
import br.com.oi.sgis.service.ComponentMovTypeService;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/component/mov-types")
@CrossOrigin(origins = "*")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ComponentMovTypeController {

    private final ComponentMovTypeService componentMovTypeService;

    @GetMapping("/signals")
    public List<SignalMovTypeDTO> signalList(){
        return Arrays.stream(SignalMovTypeEnum.values()).map(
                        itens->
                                SignalMovTypeDTO.builder().id(itens.getId()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @GetMapping("/types")
    public List<MovTypeDTO> typeList(){
        return Arrays.stream(MovTypeEnum.values()).map(
                        itens->
                                MovTypeDTO.builder().id(itens.getId()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<ComponentMovTypeDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                @RequestParam(required = false) List<String> sortAsc,
                                                                                @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<ComponentMovTypeDTO> allComponentMovType =  componentMovTypeService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allComponentMovType, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public ComponentMovTypeDTO findById(@RequestParam String id) throws ComponentMovTypeNotFoundException {
        return componentMovTypeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createComponentMovType(@Valid @RequestBody ComponentMovTypeDTO componentMovTypeDTO){
        return componentMovTypeService.createComponentMovType(componentMovTypeDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateComponentMovType(@Valid @RequestBody ComponentMovTypeDTO componentMovTypeDTO) throws ComponentMovTypeNotFoundException {
        return componentMovTypeService.updateComponentMovType(componentMovTypeDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = componentMovTypeService.componentMovTypeReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Tipo_Mov_Componente.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String id) throws ComponentMovTypeNotFoundException {
        componentMovTypeService.deleteById(id);
    }
}
