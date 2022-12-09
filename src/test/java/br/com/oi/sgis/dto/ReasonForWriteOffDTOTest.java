package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ReasonForWriteOffDTOTest {

    @Test
    void reasonForWriteOffDTOTest(){
        ReasonForWriteOffDTO reasonForWriteOffDTO = ReasonForWriteOffDTO.builder().build();
        reasonForWriteOffDTO.setCod("Cod");
        reasonForWriteOffDTO.setDescription("Description");

        assertEquals("Cod", reasonForWriteOffDTO.getCod());
        assertEquals("Description", reasonForWriteOffDTO.getDescription());
    }
}