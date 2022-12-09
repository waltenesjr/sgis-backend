package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.TicketInterventionDTO;
import br.com.oi.sgis.entity.RepSituation;
import br.com.oi.sgis.entity.TechnicalStaff;
import br.com.oi.sgis.entity.TicketIntervention;
import br.com.oi.sgis.mapper.TicketInterventionMapper;
import br.com.oi.sgis.repository.TicketInterventionRepository;
import br.com.oi.sgis.service.factory.TicketInterventionFactory;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketInterventionFactoryImpl implements TicketInterventionFactory {

    private final Validator<TicketInterventionDTO> validator;
    private final TicketInterventionRepository ticketInterventionRepository;


    @Override
    public TicketIntervention createTicketIntervention(TicketInterventionDTO ticketInterventionDTO){

        Long nextSequence = ticketInterventionRepository.getNextSequence();
        ticketInterventionDTO.setSequence(nextSequence);
        if(Boolean.TRUE.equals(ticketInterventionDTO.getExternalRepair()))
            return createExternalTicketIntervention(ticketInterventionDTO);

        return createInternalTicketIntervention(ticketInterventionDTO);
    }

    private TicketIntervention createExternalTicketIntervention(TicketInterventionDTO ticketInterventionDTO){
        TicketInterventionMapper mapper = TicketInterventionMapper.INSTANCE;
        TicketIntervention ticketIntervention = mapper.toModel(ticketInterventionDTO);
        validator.validate(ticketInterventionDTO);
        if(ticketIntervention.getInitialDate() == null) {
            ticketIntervention.setInitialDate(LocalDateTime.now());
        }
        ticketIntervention.setOperator(TechnicalStaff.builder().id(Utils.getUser().getId()).build());
        ticketIntervention.setTechnician(TechnicalStaff.builder().id(Utils.getUser().getId()).build());
        return ticketIntervention;
    }

    private TicketIntervention createInternalTicketIntervention(TicketInterventionDTO ticketInterventionDTO){
        TicketInterventionMapper mapper = TicketInterventionMapper.INSTANCE;
        TicketIntervention ticketIntervention = mapper.toModel(ticketInterventionDTO);
        if(ticketIntervention.getInitialDate() == null) {
            ticketIntervention.setInitialDate(LocalDateTime.now());
        }
        ticketIntervention.setOperator(TechnicalStaff.builder().id(Utils.getUser().getId()).build());
        ticketIntervention.setCpValue(BigDecimal.ZERO);
        ticketIntervention.setLaborValue(BigDecimal.ZERO);
        ticketIntervention.setRepSituation(RepSituation.builder().id("ERI").build());

        return ticketIntervention;
    }


}
