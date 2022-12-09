package br.com.oi.sgis.controller;

import br.com.oi.sgis.entity.Uf;
import br.com.oi.sgis.service.UfService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
class UfControllerTest {

    @InjectMocks
    private UfController ufController;

    @Mock
    private UfService ufService;

    @Test
    void shouldListAll(){
        List<Uf> listUf = new EasyRandom().objects(Uf.class,26).collect(Collectors.toList());
        Mockito.doReturn(listUf).when(ufService).listAll();

        ResponseEntity<List<Uf>> ufs = ufController.listAll();
        Assertions.assertNotNull(ufs.getBody());
        Assertions.assertEquals(listUf.size(), ufs.getBody().size());
        Assertions.assertEquals(listUf.get(0), ufs.getBody().get(0));
    }
}