package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ReporterOrderDTOTest {

    @Test
    void reporterOrderDTOTest(){
        ReporterOrderDTO reporterOrderDTO = ReporterOrderDTO.builder().build();
        reporterOrderDTO.setCod("1");
        reporterOrderDTO.setDescription("description");

        assertEquals("1", reporterOrderDTO.getCod());
        assertEquals("description", reporterOrderDTO.getDescription());
    }

}