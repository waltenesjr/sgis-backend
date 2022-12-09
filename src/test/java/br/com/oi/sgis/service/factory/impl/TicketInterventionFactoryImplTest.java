package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.CurrencyTypeDTO;
import br.com.oi.sgis.dto.PriorityRepairDTO;
import br.com.oi.sgis.dto.TicketInterventionDTO;
import br.com.oi.sgis.entity.TicketIntervention;
import br.com.oi.sgis.enums.PriorityRepairEnum;
import br.com.oi.sgis.repository.TicketInterventionRepository;
import br.com.oi.sgis.service.validator.impl.TicketInterventionValidator;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TicketInterventionFactoryImplTest {

    @Mock
    private TicketInterventionValidator ticketInterventionValidator;
    @Mock
    private TicketInterventionRepository ticketInterventionRepository;
    @InjectMocks
    private TicketInterventionFactoryImpl ticketInterventionFactory;
    private TicketInterventionDTO ticketInterventionDTO;


    @BeforeEach
    void setUp(){
        ticketInterventionDTO = new EasyRandom().nextObject(TicketInterventionDTO.class);
        ticketInterventionDTO.getRepairTicket().setPriority(PriorityRepairDTO.builder().cod(PriorityRepairEnum.A.getCod()).build());
        ticketInterventionDTO.getUnity().getFiscalDocument().setCurrencyType(CurrencyTypeDTO.builder().id("EU").build());
        ticketInterventionDTO.setTicketComponents(List.of());
        ticketInterventionDTO.setInitialDate(null);
    }

    @Test
    void createTicketIntervention() {
        ticketInterventionDTO.setExternalRepair(false);
        Mockito.doReturn(1L).when(ticketInterventionRepository).getNextSequence();
        TicketIntervention ticketInterventionCreated = ticketInterventionFactory.createTicketIntervention(ticketInterventionDTO);
        assertNotNull(ticketInterventionCreated);
    }

    @Test
    void createExternalTicketIntervention() {
        ticketInterventionDTO.setExternalRepair(true);
        Mockito.doReturn(1L).when(ticketInterventionRepository).getNextSequence();
        TicketIntervention ticketInterventionCreated = ticketInterventionFactory.createTicketIntervention(ticketInterventionDTO);
        assertNotNull(ticketInterventionCreated);
    }
}