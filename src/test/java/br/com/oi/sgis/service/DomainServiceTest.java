package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DomainDTO;
import br.com.oi.sgis.entity.Domain;
import br.com.oi.sgis.repository.DomainRepository;
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
class DomainServiceTest {

    @InjectMocks
    private DomainService domainService;

    @Mock
    private DomainRepository domainRepository;

    @Test
    void getAccountCompanyDomain() {
        List<Domain> domainsAccount = new EasyRandom().objects(Domain.class, 5).collect(Collectors.toList());
        Mockito.doReturn(domainsAccount).when(domainRepository).findAllByDomainID_Id(Mockito.any());
        List<DomainDTO> returnedDomain = domainService.getAccountCompanyDomain();
        assertEquals(domainsAccount.size(), returnedDomain.size());
    }
}