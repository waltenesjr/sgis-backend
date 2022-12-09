package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TechniqueDTOTest {

    @Test
    void techiniqueDTOTest(){
        TechniqueDTO techiniqueDTO = TechniqueDTO.builder().build();
        techiniqueDTO.setId("1233");
        techiniqueDTO.setDescription("techinique");

        Assertions.assertEquals("1233", techiniqueDTO.getId());
        Assertions.assertEquals("techinique", techiniqueDTO.getDescription());

    }
}