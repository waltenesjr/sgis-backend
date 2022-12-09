package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.ItemBySitReportTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ItemBySitReportCriteriaDTOTest {

    @Test
    void itemBySitReportCriteriaDTOTest(){
        ItemBySitReportCriteriaDTO criteriaDTO = ItemBySitReportCriteriaDTO.builder().build();
        LocalDateTime initialDates = LocalDateTime.now();
        LocalDateTime finalDates = initialDates.plusDays(100);
        criteriaDTO.setFinalMovDate(finalDates);
        criteriaDTO.setInitialMovDate(initialDates);
        criteriaDTO.setFinalRegDate(finalDates);
        criteriaDTO.setInitialRegDate(initialDates);
        criteriaDTO.setReportType(ItemBySitReportTypeEnum.ANALITICO);

        assertEquals(ItemBySitReportTypeEnum.ANALITICO.getDescription(), criteriaDTO.getReportType().getDescription());
        assertEquals(initialDates, criteriaDTO.getInitialMovDate());
        assertEquals(initialDates, criteriaDTO.getInitialRegDate());
        assertEquals(finalDates, criteriaDTO.getFinalMovDate());
        assertEquals(finalDates, criteriaDTO.getFinalRegDate());
        assertNull(criteriaDTO.getDepartment());
    }
}