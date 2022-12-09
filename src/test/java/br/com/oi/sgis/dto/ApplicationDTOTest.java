package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ApplicationDTOTest {

    @Test
    void applicationDTOTest(){
        ApplicationDTO applicationDTO = ApplicationDTO.builder().build();
        applicationDTO.setId("1233");
        applicationDTO.setDescription("application");

        Assertions.assertEquals("1233", applicationDTO.getId());
        Assertions.assertEquals("application", applicationDTO.getDescription());

    }
}