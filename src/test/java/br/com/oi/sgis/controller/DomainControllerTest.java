package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.DomainDTO;
import br.com.oi.sgis.service.DomainService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DomainControllerTest {

    @InjectMocks
    private DomainController domainController;

    @Mock
    private DomainService domainService;
    @Test
    void listAccountantCompany() {
        List<DomainDTO> domains = new EasyRandom().objects(DomainDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(domains).when(domainService).getAccountCompanyDomain();
        List<DomainDTO> returnedDomain = domainController.listAccountantCompany();
        assertEquals(domains.size(), returnedDomain.size());
    }
}