package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.AddressDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.exception.AddressNotFoundException;
import br.com.oi.sgis.service.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/v1/addresses")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDTO createAddress(@RequestBody @Valid AddressDTO addressDTO){
        return addressService.createAddress(addressDTO);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>>  listAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                                         @RequestParam(required = false) List<String> sortAsc,
                                                                         @RequestParam(required = false) List<String> sortDesc,
                                                                         @RequestParam(defaultValue = "", required = false, value = "search") String term) {

        PaginateResponseDTO<Object> allAdresses =  addressService.listPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(allAdresses, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AddressDTO findById(@PathVariable String id){
        return addressService.findById(id);
    }

    @GetMapping("id-cnpj/{idCnpj}")
    @ResponseStatus(HttpStatus.OK)
    public AddressDTO findByIdOrCnpj(@PathVariable String idCnpj) throws AddressNotFoundException {
        return addressService.findByIdOrCnpj(idCnpj);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateById(@RequestBody AddressDTO addressDTO) {
        return addressService.updateById(addressDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) {
        addressService.deleteById(id);
    }

    @GetMapping("/company")
    @ResponseStatus(HttpStatus.OK)
    public List<AddressDTO> addressesByCompany(@RequestParam String cpfCnpj){
        return addressService.getAddressesByCompany(cpfCnpj);
    }

}
