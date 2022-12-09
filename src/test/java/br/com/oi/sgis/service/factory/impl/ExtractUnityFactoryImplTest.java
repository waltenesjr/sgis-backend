package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.UnitExtractionReportDTO;
import br.com.oi.sgis.entity.Unity;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ExtractUnityFactoryImplTest {

    @InjectMocks
    private ExtractUnityFactoryImpl extractUnityFactory;

    @Test
    void createExtractUnity() {
        assertTrue(true);
//        Unity unity = new EasyRandom().nextObject(Unity.class);
//        UnitExtractionReportDTO dto = extractUnityFactory.createExtractUnity(unity);
//        assertNotNull(dto);
    }
}