package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ItemBySituationViewReportDTOTest {

    @Test
    void itemBySituationViewReportDTOTest(){
        ItemBySituationViewReportDTO itemDTO = ItemBySituationViewReportDTO.builder().build();
        itemDTO.setDepartment("department");
        itemDTO.setAnaliticList(List.of());

        assertEquals("department", itemDTO.getDepartment());
        assertEquals(0, itemDTO.getAnaliticList().size());
        assertNull(itemDTO.getTotalByDepartment());
    }
}