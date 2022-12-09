package br.com.oi.sgis.service;

import br.com.oi.sgis.entity.Uf;
import br.com.oi.sgis.repository.UfRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
class UfServiceTest {

    @InjectMocks
    private UfService ufService;

    @Mock
    private UfRepository ufRepository;

    @Test
    void shouldListAll(){
        List<Uf> listUf = new EasyRandom().objects(Uf.class,26).collect(Collectors.toList());
        Mockito.doReturn(listUf).when(ufRepository).findAll();

        List<Uf> ufReturned = ufService.listAll();
        Assertions.assertEquals(listUf.size(), ufReturned.size());
    }

}