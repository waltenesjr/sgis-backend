package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.EletricPropNotFoundException;
import br.com.oi.sgis.service.ElectricalPropService;
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
@RequestMapping("/api/v1/electrical-properties")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class ElectricalPropController {

    private final ElectricalPropService electricalPropService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ElectricalPropDTO> listAll(){
        return electricalPropService.listAll();
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<ElectricalPropDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                                                            @RequestParam(required = false) List<String> sortAsc,
                                                                            @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                            @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<ElectricalPropDTO> allElectricalProps =  electricalPropService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allElectricalProps, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ElectricalPropDTO findById(@PathVariable String id) throws EletricPropNotFoundException {
        return electricalPropService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createElectricalProp(@Valid @RequestBody ElectricalPropDTO electricalPropDTO){
        return electricalPropService.createElectricalProperty(electricalPropDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateElectricalProp(@Valid @RequestBody ElectricalPropDTO electricalPropDTO) throws EletricPropNotFoundException {
        return electricalPropService.updateElectricalProperty(electricalPropDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = electricalPropService.electricalPropReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Prop_Eletricas.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) throws EletricPropNotFoundException {
        electricalPropService.deleteById(id);
    }

    @PostMapping("/physical/filter")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<PhysicalElectricalPropsDTO>> physicalElectricalProperty(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                    @RequestParam(required = false) List<String> sortAsc,
                                                                                    @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                    @RequestBody ElectricalPropFilterDTO filterDto) {

        PaginateResponseDTO<PhysicalElectricalPropsDTO> physicalElectricalProps =  electricalPropService.physicalElectricalProperty(pageNo, pageSize, sortAsc, sortDesc,filterDto);
        return new ResponseEntity<>(physicalElectricalProps, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/physical/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> physicalElectricalPropertyReport(
                                                                                                      @RequestParam(required = false) List<String> sortAsc,
                                                                                                      @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                                      @RequestBody ElectricalPropFilterDTO filterDto) throws JRException, IOException {

        byte[] report =   electricalPropService.physicalElectricalPropertyReport(sortAsc, sortDesc,filterDto);
        String filename ="Relatorio_Prop_Eletricas_Fisicas.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }
}
