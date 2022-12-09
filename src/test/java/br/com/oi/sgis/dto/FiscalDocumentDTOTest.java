package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;


@ExtendWith(MockitoExtension.class)
class FiscalDocumentDTOTest {
    @Test
    void fiscalDocumentDTOTest() {

        LocalDateTime date = LocalDateTime.now();
        FiscalDocumentDTO fiscalDocumentDTO = FiscalDocumentDTO.builder().docDate(date)
                .companyId("123456")
                .companyName("Entreprise 101")
                .docNumber(123456L)
                .build();

        Assertions.assertEquals(date.withHour(0).withMinute(0).withSecond(0).withMinute(0).withNano(0),
                fiscalDocumentDTO.getDocDate());
        Assertions.assertEquals("123456", fiscalDocumentDTO.getCompanyId());
        Assertions.assertEquals("Entreprise 101", fiscalDocumentDTO.getCompanyName());
        Assertions.assertEquals(123456L, fiscalDocumentDTO.getDocNumber());

    }


}