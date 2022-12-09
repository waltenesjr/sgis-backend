package br.com.oi.sgis.entity.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MovItensViewTest {

    @Test
    void movItensViewTest(){
        MovItensView movItensView = MovItensView.builder().build();

        movItensView.setId(1233L);
        movItensView.setBarcode("15244");
        movItensView.setUnityCode("12345");
        movItensView.setMnemonic("147");
        movItensView.setDescription("description");
        movItensView.setFromResponsible("RJ-ARC");
        movItensView.setToResponsible("MA-ARC");
        movItensView.setFromSituationCode("DIS");
        movItensView.setToSituationCode("DEF");
        movItensView.setToTechnician("RJ15454");
        movItensView.setFromTechnician("RJ15454");
        movItensView.setDate(LocalDateTime.of(2022, 02,14,00,00));

        assertEquals(1233L, movItensView.getId() );
        assertEquals("15244", movItensView.getBarcode());
        assertEquals("12345", movItensView.getUnityCode() );
        assertEquals("147", movItensView.getMnemonic() );
        assertEquals( "description", movItensView.getDescription());
        assertEquals("RJ-ARC", movItensView.getFromResponsible());
        assertEquals("MA-ARC", movItensView.getToResponsible());
        assertEquals("DIS", movItensView.getFromSituationCode());
        assertEquals("DEF", movItensView.getToSituationCode());
        assertEquals("RJ15454", movItensView.getToTechnician());
        assertEquals("RJ15454", movItensView.getFromTechnician());
        assertEquals("14/02/2022", movItensView.getDate());
    }
}