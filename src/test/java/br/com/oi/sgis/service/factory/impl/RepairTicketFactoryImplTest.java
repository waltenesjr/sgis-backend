package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.PriorityRepairDTO;
import br.com.oi.sgis.dto.RepairTicketDTO;
import br.com.oi.sgis.entity.RepairTicket;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.RepairTicketException;
import br.com.oi.sgis.mapper.RepairTicketMapper;
import br.com.oi.sgis.repository.RepairTicketRepository;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.PendencyService;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.Year;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RepairTicketFactoryImplTest {

    @Mock
    private Validator<RepairTicket> validator;
    @Mock
    private PendencyService pendencyService;
    @Mock
    private RepairTicketRepository repairTicketRepository;
    @Mock
    private UnityRepository unityRepository;
    @MockBean
    private RepairTicketMapper repairTicketMapper = RepairTicketMapper.INSTANCE;

    @InjectMocks
    private RepairTicketFactoryImpl repairTicketFactory;

    String newTicketNumber() {
        DecimalFormat df = new DecimalFormat("000000");
        String uf = "RJ";
        String lastId =  "RJ2022011111";
        String year = lastId.substring(2,6);
        String lastNumber = lastId.substring(6);
        if(!Year.now().toString().equals(year)) {
            year = Year.now().toString();
            lastNumber = df.format(1);
        }else {
            lastNumber = df.format(new BigInteger(lastNumber).add(BigInteger.ONE));
        }
        return uf + year + lastNumber;
    }

    @Test
    void testCreateRepairTicket() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        repairTicketDTO.setPriority(PriorityRepairDTO.builder().cod("A").build());
        RepairTicket repairTicket = repairTicketMapper.toModel(repairTicketDTO);
        repairTicket.setId(newTicketNumber());
        Unity unity =new EasyRandom().nextObject(Unity.class);
        String lastId =  "RJ2022011111";
        repairTicketDTO.setOriginDepartment(Utils.getUser().getDepartmentCode().getId());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(lastId).when(repairTicketRepository).findTop1ByIdDesc();
        Mockito.doReturn(repairTicket).when(repairTicketRepository).save(Mockito.any());
        RepairTicket generate = repairTicketFactory.createRepairTicket(repairTicketDTO);
        assertEquals(repairTicketDTO.getBaNumber(), generate.getBaNumber());
        assertEquals("RJ2022011112", generate.getId());
        Mockito.verify(pendencyService, Mockito.times(1)).createPendencyFromRepairTicket(repairTicket);
    }


    @Test
    void testCreateRepairTicketExceptionSameDevolutionDepartment() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        repairTicketDTO.setOriginDepartment(Utils.getUser().getDepartmentCode().getId());
        repairTicketDTO.setDevolutionDepartment(Utils.getUser().getDepartmentCode().getId());
        repairTicketDTO.setPriority(PriorityRepairDTO.builder().cod("A").build());

        RepairTicket repairTicket = repairTicketMapper.toModel(repairTicketDTO);
        repairTicket.setId(newTicketNumber());
        Unity unity =new EasyRandom().nextObject(Unity.class);
        String lastId =  "RJ2022011111";
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(lastId).when(repairTicketRepository).findTop1ByIdDesc();
        Mockito.doReturn(repairTicket).when(repairTicketRepository).save(Mockito.any());
        Exception e = assertThrows(RepairTicketException.class,()->repairTicketFactory.createRepairTicket(repairTicketDTO));
        assertEquals(MessageUtils.REPAIR_TICKET_PENDENCY_ERROR.getDescription(), e.getMessage());

    }

    @Test
    void testCreateRepairTicketException() {
        RepairTicketDTO repairTicketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        repairTicketDTO.setOriginDepartment(Utils.getUser().getDepartmentCode().getId());
        repairTicketDTO.setDevolutionDepartment(Utils.getUser().getDepartmentCode().getId());
        repairTicketDTO.setPriority(PriorityRepairDTO.builder().cod("A").build());
        RepairTicket repairTicket = repairTicketMapper.toModel(repairTicketDTO);
        repairTicket.setId(newTicketNumber());
        Unity unity =new EasyRandom().nextObject(Unity.class);
        String lastId =  "RJ2019011111";
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(lastId).when(repairTicketRepository).findTop1ByIdDesc();
        Mockito.doThrow(InvalidJpaQueryMethodException.class).when(repairTicketRepository).save(Mockito.any());
        Exception e = assertThrows(RepairTicketException.class,()->repairTicketFactory.createRepairTicket(repairTicketDTO));
        assertEquals(MessageUtils.REPAIR_TICKET_ERROR.getDescription(), e.getMessage());

    }
}