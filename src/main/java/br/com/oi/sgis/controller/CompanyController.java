package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.exception.CompanyNotFoundException;
import br.com.oi.sgis.service.CompanyService;
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
@RequestMapping("/api/v1/companies")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<CompanyDTO>> listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                  @RequestParam(required = false) List<String> sortAsc,
                                                                                  @RequestParam(required = false,defaultValue = "id") List<String> sortDesc,
                                                                                  @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<CompanyDTO> allCompanies =  companyService.listAllPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allCompanies, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/without-parameter")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<CompanyDTO>> listAllCompanyWithoutParameterPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                                   @RequestParam(required = false) List<String> sortAsc,
                                                                                                   @RequestParam(required = false,defaultValue = "id") List<String> sortDesc,
                                                                                                   @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<CompanyDTO> allCompanies =  companyService.listAllCompanyWithoutParameterPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allCompanies, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public CompanyDTO findById(@PathVariable String id) throws CompanyNotFoundException {
        return companyService.findById(id);
    }

    @GetMapping("/operators/active")
    public ResponseEntity<PaginateResponseDTO<CompanyDTO>> operatorsActive(@RequestParam(defaultValue = "1") Integer pageNo,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @RequestParam(required = false) List<String> sortAsc,
                                            @RequestParam(required = false,defaultValue = "id") List<String> sortDesc,
                                            @RequestParam(defaultValue = "", required = false, value = "search") String term) {
        PaginateResponseDTO<CompanyDTO> allCompanies =  companyService.listAllOperatorActives(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allCompanies, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createCompany(@Valid @RequestBody CompanyDTO companyDTO){
        return companyService.createCompany(companyDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateCompany(@Valid @RequestBody CompanyDTO companyDTO) throws CompanyNotFoundException {
        return companyService.updateCompany(companyDTO);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> report(@RequestBody ReportCrudSearchDTO reportDto) throws JRException, IOException {
        byte[] report = companyService.companyReport(reportDto.getSearch(), reportDto.getSortAsc(), reportDto.getSortDesc());
        String filename ="Relatorio_Empresas.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @PostMapping("/transfer-provider")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO transferProvider(@Valid @RequestBody TransferProviderDTO transferProvider) throws CompanyNotFoundException {
        return companyService.transferProvider(transferProvider);
    }

    @PostMapping("/installation-client/provider")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO instClientByProvider(@Valid @RequestBody ClientInstByProviderDTO clientInstByProviderDTO) throws CompanyNotFoundException {
        return companyService.instClientByProvider(clientInstByProviderDTO);
    }

    @GetMapping("/clients")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<CompanyDTO>> listAllClients(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                                                            @RequestParam(required = false) List<String> sortAsc,
                                                                            @RequestParam(required = false,defaultValue = "id") List<String> sortDesc,
                                                                            @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<CompanyDTO> allCompanies =  companyService.listAllClients(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allCompanies, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/installation-client/technician")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO instClientByTechnician(@Valid @RequestBody ClientInstByProviderDTO clientInstByProviderDTO) throws CompanyNotFoundException {
        return companyService.instClientByTechnician(clientInstByProviderDTO);
    }

    @PostMapping("/provider/emit-proof")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> emitProofProvider(@RequestBody EmitProofProviderDTO emitProofProviderDTO) throws  JRException, IOException, CompanyNotFoundException {
        byte[] report = companyService.emitProofProvider(emitProofProviderDTO);
        String filename ="Comprovante_Prestador.pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @GetMapping("/users-extraction")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<CompanyDTO>> listAllForUsersExtraction(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                                                            @RequestParam(required = false) List<String> sortAsc,
                                                                            @RequestParam(required = false,defaultValue = "id") List<String> sortDesc,
                                                                            @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<CompanyDTO> allCompanies =  companyService.listAllForUsersExtraction(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allCompanies, new HttpHeaders(), HttpStatus.OK);
    }
}
