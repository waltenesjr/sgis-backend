package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.ModelEquipTypeDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.ModelEquipTypeNotFound;
import br.com.oi.sgis.service.ModelEquipTypeService;
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
@RequestMapping("/api/v1/models-equipaments")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class ModelEquipTypeController {

    private final ModelEquipTypeService modelEquipTypeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<ModelEquipTypeDTO>> listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                                        @RequestParam(required = false) List<String> sortAsc,
                                                                        @RequestParam(required = false, defaultValue = "id") List<String> sortDesc,
                                                                        @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<ModelEquipTypeDTO> allmodelEquipTypes = modelEquipTypeService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc, term);
        return new ResponseEntity<>(allmodelEquipTypes, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public ModelEquipTypeDTO findById(@RequestParam String id) throws ModelEquipTypeNotFound {
        return modelEquipTypeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createModelEquipType(@Valid @RequestBody ModelEquipTypeDTO modelEquipTypeDTO){
        return modelEquipTypeService.createModelEquipType(modelEquipTypeDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateModelEquipType(@Valid @RequestBody ModelEquipTypeDTO modelEquipTypeDTO) throws ModelEquipTypeNotFound {
        return modelEquipTypeService.updateModelEquipType(modelEquipTypeDTO);
    }

    @PutMapping("/update/equipament-flag")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateDescontEquip(@RequestBody ModelEquipTypeDTO modelEquipTypeDTO) throws ModelEquipTypeNotFound {
        return modelEquipTypeService.updateDescontEquip(modelEquipTypeDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = modelEquipTypeService.modelEquipTypeReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Modelo_Equip.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String id) throws ModelEquipTypeNotFound {
        modelEquipTypeService.deleteById(id);
    }

}
