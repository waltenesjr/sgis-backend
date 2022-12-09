package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.TicketIntervExtraInfoDTO;
import br.com.oi.sgis.dto.TicketRepExtraInfoDTO;
import br.com.oi.sgis.entity.ComponentMov;
import br.com.oi.sgis.entity.RepairTicket;
import br.com.oi.sgis.entity.TicketDiagnosis;
import br.com.oi.sgis.enums.PriorityRepairEnum;
import br.com.oi.sgis.mapper.ComponentMovMapper;
import br.com.oi.sgis.mapper.TicketDiagnosisMapper;
import br.com.oi.sgis.repository.ComponentMovRepository;
import br.com.oi.sgis.repository.TicketDiagnosisRepository;
import br.com.oi.sgis.repository.TicketInterventionRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TicketRepExtraInfoServiceTest {

    @InjectMocks
    private TicketRepExtraInfoService ticketRepExtraInfoService;
    @Mock
    private TicketInterventionRepository ticketInterventionRepository;
    @Mock
    private TicketDiagnosisRepository ticketDiagnosisRepository;
    @Mock
    private ComponentMovRepository componentMovRepository;
    @MockBean
    private static final TicketDiagnosisMapper ticketDiagnosisMapper = TicketDiagnosisMapper.INSTANCE;
    @MockBean
    private static final ComponentMovMapper componentMovMapper = ComponentMovMapper.INSTANCE;

    @Test
    void getExtraInfo() {
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        repairTicket.setPriority(PriorityRepairEnum.M);
        List<ComponentMov> componentMovDTOS = new EasyRandom().objects(ComponentMov.class, 1).collect(Collectors.toList());
        List<TicketDiagnosis> ticketDiagnoses = new EasyRandom().objects(TicketDiagnosis.class, 1).collect(Collectors.toList());
        List<TicketIntervExtraInfoDTO> ticketInterventionDTOS = new EasyRandom().objects(TicketIntervExtraInfoDTO.class, 1).collect(Collectors.toList());

        Mockito.doReturn(componentMovDTOS).when(componentMovRepository).findAllByTicketIntervention(Mockito.any());
        Mockito.doReturn(ticketDiagnoses).when(ticketDiagnosisRepository).findAllByTicketIntervention(Mockito.any());
        Mockito.doReturn(ticketInterventionDTOS).when(ticketInterventionRepository).findByRepairTicket(Mockito.any());

        TicketRepExtraInfoDTO returnedInfo = ticketRepExtraInfoService.getExtraInfo(repairTicket);

        assertEquals(repairTicket.getRepairValue(), returnedInfo.getValue());
        assertEquals(repairTicket.getOpenDate(), returnedInfo.getOpenDate());
        assertEquals(ticketDiagnoses.size(), returnedInfo.getDiagnoses().size());
        assertEquals(componentMovDTOS.size(), returnedInfo.getComponentMov().size());
    }
}