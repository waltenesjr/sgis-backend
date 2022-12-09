package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DomainDTO;
import br.com.oi.sgis.mapper.DomainMapper;
import br.com.oi.sgis.repository.DomainRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DomainService {

    private final DomainRepository domainRepository;
    private static final DomainMapper domainMapper = DomainMapper.INSTANCE;
    private static String ACCOUNTANT_COMPANY_KEY = "UN_EMP_CONTABIL";

    public List<DomainDTO> getAccountCompanyDomain(){
        return domainRepository.findAllByDomainID_Id(ACCOUNTANT_COMPANY_KEY).stream().map(domainMapper::toDTO).collect(Collectors.toList());
    }
}
