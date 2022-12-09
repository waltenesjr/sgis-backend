package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.ContractDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.exception.ContractNotFoundException;
import br.com.oi.sgis.service.ContractService;
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
@RequestMapping("/api/v1/contracts")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class ContractController {
    private final ContractService contractService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<ContractDTO>> listAll(@RequestParam(defaultValue = "1") Integer pageNo,
                                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                                               @RequestParam(required = false) List<String> sortAsc,
                                                               @RequestParam(required = false,defaultValue = "id") List<String> sortDesc,
                                                               @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<ContractDTO> allContracts =  contractService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allContracts, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/contract")
    public ContractDTO findById(@RequestParam String id) throws ContractNotFoundException {
        return contractService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createContract(@Valid @RequestBody ContractDTO contractDTO){
        return contractService.createContract(contractDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateContract(@Valid  @RequestBody ContractDTO contractDTO) throws ContractNotFoundException {
        return contractService.updateContract(contractDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = contractService.contractReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Contratos.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }


    @DeleteMapping("/contract")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@RequestParam String id) throws ContractNotFoundException {
        contractService.deleteById(id);
    }

    @GetMapping("/forward-repair")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<ContractDTO>> listForwardRepair(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                                    @RequestParam(required = false) List<String> sortAsc,
                                                                    @RequestParam(required = false,defaultValue = "id") List<String> sortDesc,
                                                                    @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<ContractDTO> allContracts =  contractService.listForwardRepair(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allContracts, new HttpHeaders(), HttpStatus.OK);
    }
}
