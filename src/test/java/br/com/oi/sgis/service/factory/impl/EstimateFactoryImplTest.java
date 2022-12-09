package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.EstimateDTO;
import br.com.oi.sgis.entity.Estimate;
import br.com.oi.sgis.entity.TicketIntervention;
import br.com.oi.sgis.mapper.EstimateMapper;
import br.com.oi.sgis.repository.EstimateRepository;
import br.com.oi.sgis.repository.ItemEstimateRepository;
import br.com.oi.sgis.repository.TicketInterventionRepository;
import br.com.oi.sgis.service.factory.TicketInterventionFactory;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class EstimateFactoryImplTest {
    @InjectMocks
    private EstimateFactoryImpl estimateFactory;
    @Mock
    private EstimateRepository estimateRepository;
    @Mock
    private Validator<EstimateDTO> validator;
    @Mock
    private ItemEstimateRepository itemEstimateRepository;
    @Mock
    private TicketInterventionRepository ticketInterventionRepository;
    @Mock
    private TicketInterventionFactory ticketInterventionFactory;


    @Test
    void createEstimate() {
        EstimateMapper estimateMapper = EstimateMapper.INSTANCE;
        TicketIntervention ticketIntervention = new EasyRandom().nextObject(TicketIntervention.class);
        Mockito.doReturn(ticketIntervention).when(ticketInterventionFactory).createTicketIntervention(Mockito.any());
        Mockito.doReturn(ticketIntervention).when(ticketInterventionRepository).save(Mockito.any());

        Mockito.doReturn("RJ000001").when(estimateRepository).findTop1ByIdDesc(Mockito.any());
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        Mockito.doReturn(estimate).when(estimateRepository).save(Mockito.any());
        EstimateDTO estimateDTO = estimateMapper.toDTO(estimate);

        Estimate createdEstimate = estimateFactory.createEstimate(estimateDTO);
        assertNotNull(createdEstimate);
    }

    @Test
    void createEstimateException() {
        EstimateMapper estimateMapper = EstimateMapper.INSTANCE;
        Mockito.doReturn("RJ000001").when(estimateRepository).findTop1ByIdDesc(Mockito.any());
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        Mockito.doThrow(SqlScriptParserException.class).when(estimateRepository).save(Mockito.any());
        EstimateDTO estimateDTO = estimateMapper.toDTO(estimate);

        Exception e = assertThrows(IllegalArgumentException.class, () -> estimateFactory.createEstimate(estimateDTO));
        assertEquals(MessageUtils.ESTIMATE_SAVE_ERROR.getDescription(), e.getMessage());
    }

}