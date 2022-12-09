package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.UserExtractionDTO;
import br.com.oi.sgis.service.UserRegisterService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserRegisterControllerTest {

    @InjectMocks
    private UserRegisterController userRegisterController;

    @Mock
    private UserRegisterService userRegisterService;

    @Test
    void unitExtractionReport() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(userRegisterService).userExtractionReport(Mockito.any());
        UserExtractionDTO userExtractionDTO = new EasyRandom().nextObject(UserExtractionDTO.class);
        ResponseEntity<byte[]> responseReport = userRegisterController.userExtractionReport(userExtractionDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }
}