package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyDTOTest {

    @Test
    void companyDTOTest(){
        CompanyDTO companyDTO = CompanyDTO.builder().build();
        companyDTO.setId("1233");
        companyDTO.setCompanyName("Company");
        companyDTO.setStateRegistration("124545");
        companyDTO.setTradeName("Le company");

        Assertions.assertEquals("1233", companyDTO.getId());
        Assertions.assertEquals("Company", companyDTO.getCompanyName());
        Assertions.assertEquals("124545", companyDTO.getStateRegistration());
        Assertions.assertEquals("Le company", companyDTO.getTradeName());

    }

}