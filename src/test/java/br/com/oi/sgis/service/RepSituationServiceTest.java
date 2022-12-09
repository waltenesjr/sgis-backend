package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.entity.RepSituation;
import br.com.oi.sgis.exception.RepSituationNotFoundException;
import br.com.oi.sgis.repository.RepSituationRepository;
import br.com.oi.sgis.util.MessageUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RepSituationServiceTest {

    @InjectMocks
    private RepSituationService repSituationService;

    @Mock
    private RepSituationRepository repSituationRepository;

    @Test
    void listAll() {
        List<RepSituation> repSituations = new EasyRandom().objects(RepSituation.class, 5).collect(Collectors.toList());

        Mockito.doReturn(repSituations).when(repSituationRepository).findAll();
        List<SituationDTO>repSituationDTOSToReturn = repSituationService.listAll();
        assertEquals(repSituations.size(), repSituationDTOSToReturn.size());
    }

    @Test
    void findById() throws RepSituationNotFoundException {
        RepSituation repSituation = new EasyRandom().nextObject(RepSituation.class);

        Mockito.doReturn(Optional.of(repSituation)).when(repSituationRepository).findById(Mockito.any());
        SituationDTO repSituationToReturn = repSituationService.findById("1L");

        assertEquals(repSituation.getId(), repSituationToReturn.getId());
    }
    @Test

    void shouldFindByIdWithException() {
        Mockito.doReturn(Optional.empty()).when(repSituationRepository).findById(Mockito.any());

        Exception e = assertThrows(RepSituationNotFoundException.class, () -> repSituationService.findById("1L"));
        assertEquals(MessageUtils.REP_SITUATION_NOT_FOUND_BY_ID.getDescription() + "1L", e.getMessage());
    }

    @Test
    void listForwardRepair() {
        List<RepSituation> repSituations = new EasyRandom().objects(RepSituation.class, 5).collect(Collectors.toList());

        Mockito.doReturn(repSituations).when(repSituationRepository).listSituationsForwardRepair();
        List<SituationDTO>repSituationDTOSToReturn = repSituationService.listForwardRepair();
        assertEquals(repSituations.size(), repSituationDTOSToReturn.size());
    }

    @Test
    void listForwardRepairFromInterv() {
        List<RepSituation> repSituations = new EasyRandom().objects(RepSituation.class, 5).collect(Collectors.toList());

        Mockito.doReturn(repSituations).when(repSituationRepository).listSelectedSituations(Mockito.any());
        List<SituationDTO>repSituationDTOSToReturn = repSituationService.listForwardRepairFromInterv();
        assertEquals(repSituations.size(), repSituationDTOSToReturn.size());
    }
}