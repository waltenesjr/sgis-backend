package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class InstallationReasonDTOTest {

    @Test
    void installationReasonDTOTest(){
        InstallationReasonDTO installationReasonDTO = InstallationReasonDTO.builder().build();
        installationReasonDTO.setCod("COD");
        installationReasonDTO.setDescription("Description");

        assertEquals("COD", installationReasonDTO.getCod());
        assertEquals("Description", installationReasonDTO.getDescription());
    }

}