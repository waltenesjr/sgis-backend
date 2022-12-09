package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.EstimateDTO;
import br.com.oi.sgis.entity.Estimate;
import br.com.oi.sgis.mapper.EstimateMapper;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class EstimateValidatorTest {

    @InjectMocks
    private EstimateValidator estimateValidator;

    @Test
    void validate() {
        EstimateMapper estimateMapper = EstimateMapper.INSTANCE;
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        EstimateDTO estimateDTO = estimateMapper.toDTO(estimate);
        estimateDTO.setDepartment(Utils.getUser().getDepartmentCode());
        estimateDTO.setDate(LocalDateTime.now());
        estimateDTO.setExpirationDate(LocalDateTime.now().plusDays(5));
        assertDoesNotThrow(()->estimateValidator.validate(estimateDTO));
    }

    @Test
    void validateRepairCenter() {
        EstimateMapper estimateMapper = EstimateMapper.INSTANCE;
        Estimate estimate = new EasyRandom().nextObject(Estimate.class);
        EstimateDTO estimateDTO = estimateMapper.toDTO(estimate);
        estimateDTO.setDate(LocalDateTime.now());
        estimateDTO.setExpirationDate(LocalDateTime.now().plusDays(5));
        Exception e = assertThrows(IllegalArgumentException.class, ()->estimateValidator.validate(estimateDTO));
        assertEquals(MessageUtils.ESTIMATE_REPAIR_CENTER_ERROR.getDescription(), e.getMessage());
    }
}