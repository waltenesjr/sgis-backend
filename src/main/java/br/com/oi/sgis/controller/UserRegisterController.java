package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.UserExtractionDTO;
import br.com.oi.sgis.service.UserRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserRegisterController {

    private final UserRegisterService userRegisterService;

    @PostMapping("/extraction")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> userExtractionReport(@RequestBody UserExtractionDTO userExtractionDTO) {
        byte[] report = userRegisterService.userExtractionReport(userExtractionDTO);
        String filename ="Extracao_Usuarios.xlsx";
        return  ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(report);
    }
}
