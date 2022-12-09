package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.EquipamentTypeDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.EquipamentTypeNotFoundException;
import br.com.oi.sgis.service.EquipamentTypeService;
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
@RequestMapping("/api/v1/equipaments-type")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class EquipamentTypeController {

    private final EquipamentTypeService equipamentTypeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<EquipamentTypeDTO>> listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                    @RequestParam(required = false) List<String> sortAsc,
                                                                                    @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                                    @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<EquipamentTypeDTO> allTypes =  equipamentTypeService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allTypes, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public EquipamentTypeDTO findById(@RequestParam String id) throws EquipamentTypeNotFoundException {
        return equipamentTypeService.findById(id);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = equipamentTypeService.equipamentTypeReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Tipos_Equipamento.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createEquipamentType(@Valid @RequestBody EquipamentTypeDTO equipamentTypeDTO){
        return equipamentTypeService.createEquipamentType(equipamentTypeDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateEquipamentType(@Valid @RequestBody EquipamentTypeDTO equipamentTypeDTO) throws EquipamentTypeNotFoundException {
        return equipamentTypeService.updateEquipamentType(equipamentTypeDTO);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String id) throws EquipamentTypeNotFoundException {
        equipamentTypeService.deleteById(id);
    }
}
