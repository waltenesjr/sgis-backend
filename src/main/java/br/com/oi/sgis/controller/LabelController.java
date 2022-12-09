package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.LabelParametersDTO;
import br.com.oi.sgis.dto.PackingLabelParametersDTO;
import br.com.oi.sgis.dto.PrintLabelTypeDTO;
import br.com.oi.sgis.enums.LabelPrintTypeEnum;
import br.com.oi.sgis.service.LabelService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/labels")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class LabelController {

    private final LabelService labelService;

    @GetMapping("/print-type")
    public List<PrintLabelTypeDTO> typeList(){
        return Arrays.stream(LabelPrintTypeEnum.values()).map(
                        itens->
                                PrintLabelTypeDTO.builder().cod(itens.getCod()).description(itens.getDescription()).build())
                .collect(Collectors.toList());
    }

    @GetMapping("/box")
    public LabelParametersDTO lastBoxLabel() {
        return labelService.getLastBoxLabel();
    }

    @GetMapping("/item")
    public LabelParametersDTO lastItemLabel() {
        return labelService.getLastItemLabel();
    }

    @PostMapping("/box")
    public ResponseEntity<byte[]> boxLabel(@RequestBody LabelParametersDTO labelParametersDTO) {
        byte[] report = labelService.boxLabels(labelParametersDTO);
        String filename ="Etiquetas_Caixa.pdf";
        return getBodyResponse(report, filename);
    }
    @PostMapping("/item")
    public ResponseEntity<byte[]> itemLabel(@RequestBody LabelParametersDTO labelParametersDTO) {
        byte[] report = labelService.itemLabels(labelParametersDTO);
        String filename ="Etiquetas_Item.pdf";
        return getBodyResponse(report, filename);
    }

    @GetMapping("/packing")
    public PackingLabelParametersDTO getPackingLabel() {
        return labelService.getPackingLabel();
    }

    @PostMapping("/packing")
    public ResponseEntity<byte[]> packingLabel(@RequestBody PackingLabelParametersDTO parametersDTO) {
        byte[] report = labelService.packingLabel(parametersDTO);
        String filename ="Etiquetas_Embalagem.pdf";
        return getBodyResponse(report, filename);
    }

    private ResponseEntity<byte[]> getBodyResponse(byte[] report, String filename) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }
}
