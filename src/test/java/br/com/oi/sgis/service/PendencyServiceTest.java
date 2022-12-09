package br.com.oi.sgis.service;

import br.com.oi.sgis.entity.RepairTicket;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.repository.PendencyRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;


@ExtendWith(MockitoExtension.class)
class PendencyServiceTest {

    @InjectMocks
    private PendencyService pendencyService;

    @Mock
    private PendencyRepository pendencyRepository;

    @Test
    void createPendencyFromRepairTicket() {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);

        pendencyService.createPendencyFromRepairTicket(repairTicket);
        Mockito.verify(pendencyRepository, Mockito.times(1)).insertNewPendency(eq(repairTicket.getUnity().getUnityCode().getId()),
                eq(repairTicket.getUnity().getId()), eq(SituationEnum.REP.getCod()), eq(repairTicket.getOriginDepartment().getId()),
                eq(repairTicket.getDevolutionDepartment().getId()),eq(repairTicket.getTechnician().getId()),
                eq(repairTicket.getOperator().getId()), eq("BR: " + repairTicket.getId()), Mockito.any());
    }

    @Test
    void createPendencyFromRepairTicketWithException() {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);

        Mockito.doThrow(InvalidJpaQueryMethodException.class).when(pendencyRepository).insertNewPendency(eq(repairTicket.getUnity().getUnityCode().getId()),
                eq(repairTicket.getUnity().getId()), eq(SituationEnum.REP.getCod()), eq(repairTicket.getOriginDepartment().getId()),
                eq(repairTicket.getDevolutionDepartment().getId()),eq(repairTicket.getTechnician().getId()),
                eq(repairTicket.getOperator().getId()), eq("BR: " + repairTicket.getId()), Mockito.any());

        assertThrows(IllegalArgumentException.class, () -> pendencyService.createPendencyFromRepairTicket(repairTicket));

    }
}