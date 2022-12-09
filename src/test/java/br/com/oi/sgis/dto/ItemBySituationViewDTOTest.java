package br.com.oi.sgis.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemBySituationViewDTOTest {

    private  ItemBySituationViewDTO itemDTO;
    @BeforeEach
    void setUp(){
        itemDTO = ItemBySituationViewDTO.builder().build();
        itemDTO.setTrn(0L);
        itemDTO.setDis(0L);
        itemDTO.setEmp(0L);
        itemDTO.setTdr(0L);
        itemDTO.setEmu(0L);
        itemDTO.setTrr(0L);
        itemDTO.setBxp(0L);
        itemDTO.setTrp(0L);
        itemDTO.setTre(0L);
        itemDTO.setRep(0L);
        itemDTO.setBxu(0L);
        itemDTO.setBxe(0L);
        itemDTO.setDvr(0L);
        itemDTO.setTrd(0L);
        itemDTO.setUso(0L);
        itemDTO.setBxi(0L);
        itemDTO.setPre(0L);
        itemDTO.setBxc(0L);
        itemDTO.setBxo(0L);
        itemDTO.setRes(0L);
        itemDTO.setOfe(0L);
        itemDTO.setDef(0L);
        itemDTO.setTdd(5L);
        itemDTO.setCodUnity("cod");
        itemDTO.setDepartmentCode("depart");
    }

    @Test
    void itemBySituationViewDTOTestTotals(){
        assertEquals(0L, itemDTO.getTrn());
        assertEquals(0L, itemDTO.getTrn());
        assertEquals(0L, itemDTO.getDis());
        assertEquals(0L, itemDTO.getEmp());
        assertEquals(0L, itemDTO.getTdr());
        assertEquals(0L, itemDTO.getEmu());
        assertEquals(0L, itemDTO.getTrr());
        assertEquals(0L, itemDTO.getBxp());
        assertEquals(0L, itemDTO.getTrp());
        assertEquals(0L, itemDTO.getTre());
        assertEquals(0L, itemDTO.getRep());
        assertEquals(0L, itemDTO.getBxu());
        assertEquals(0L, itemDTO.getBxe());
        assertEquals(0L, itemDTO.getDvr());
        assertEquals(0L, itemDTO.getTrd());
        assertEquals(0L, itemDTO.getUso());
        assertEquals(0L, itemDTO.getBxi());
        assertEquals(0L, itemDTO.getPre());
        assertEquals(0L, itemDTO.getBxc());
        assertEquals(0L, itemDTO.getBxo());
        assertEquals(0L, itemDTO.getRes());
        assertEquals(0L, itemDTO.getOfe());
        assertEquals(0L, itemDTO.getDef());
        assertEquals(5L, itemDTO.getTdd());
        assertEquals(5L, itemDTO.getTotal());

    }
    @Test
    void itemBySituationViewDTOTestInformation(){
        assertEquals("cod", itemDTO.getCodUnity());
        assertEquals("depart", itemDTO.getDepartmentCode());
    }


}