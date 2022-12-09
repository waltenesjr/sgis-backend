package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepoPropertyDTOTest {

    @Test
    void repoPropertyDTOTest(){
        RepoPropertyDTO repoPropertyDTO = RepoPropertyDTO.builder().build();
        repoPropertyDTO.setIdStation("445");
        repoPropertyDTO.setLocation("02.01");
        repoPropertyDTO.setIdUser("123");
        repoPropertyDTO.setIdUnity("4545454");

        Assertions.assertEquals("445", repoPropertyDTO.getIdStation());
        Assertions.assertEquals("02.01", repoPropertyDTO.getLocation());
        Assertions.assertEquals("123", repoPropertyDTO.getIdUser());
        Assertions.assertEquals("4545454", repoPropertyDTO.getIdUnity());
        Assertions.assertNull(repoPropertyDTO.getOriginUf());
    }


}