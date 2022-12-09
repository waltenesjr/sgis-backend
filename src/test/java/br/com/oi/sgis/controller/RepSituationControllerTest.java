package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.entity.RepSituation;
import br.com.oi.sgis.service.RepSituationService;
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
class RepSituationControllerTest {

    @InjectMocks
    private RepSituationController repSituationController;

    @Mock
    private RepSituationService repSituationService;

    @Test
    void listAll() {
        List<RepSituation> repSituations = new EasyRandom().objects(RepSituation.class, 5).collect(Collectors.toList());

        Mockito.doReturn(repSituations).when(repSituationService).listAll();
        List<SituationDTO>repSituationDTOSToReturn = repSituationController.listAll();
        assertEquals(repSituations.size(), repSituationDTOSToReturn.size());
    }

    @Test
    void listForwardRepair() {
        List<RepSituation> repSituations = new EasyRandom().objects(RepSituation.class, 5).collect(Collectors.toList());

        Mockito.doReturn(repSituations).when(repSituationService).listForwardRepair();
        List<SituationDTO>repSituationDTOSToReturn = repSituationController.listForwardRepair();
        assertEquals(repSituations.size(), repSituationDTOSToReturn.size());
    }

    @Test
    void listForwardRepairFromInterv() {
        List<RepSituation> repSituations = new EasyRandom().objects(RepSituation.class, 5).collect(Collectors.toList());

        Mockito.doReturn(repSituations).when(repSituationService).listForwardRepairFromInterv();
        List<SituationDTO>repSituationDTOSToReturn = repSituationController.listForwardRepairFromInterv();
        assertEquals(repSituations.size(), repSituationDTOSToReturn.size());
    }
}