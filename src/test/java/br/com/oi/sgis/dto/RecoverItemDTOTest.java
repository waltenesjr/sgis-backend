package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class RecoverItemDTOTest {

    @Test
    void recoverItemDTO(){
        RecoverItemDTO recoverItemDTO = RecoverItemDTO.builder().build();
        recoverItemDTO.setLocation("123");
        recoverItemDTO.setStationId("stationId");
        recoverItemDTO.setUnityId("unity");

        assertEquals("123", recoverItemDTO.getLocation());
        assertEquals("stationId", recoverItemDTO.getStationId());
        assertEquals("unity", recoverItemDTO.getUnityId());
    }

    @Test
    void registerBoDTOTestUnityIdNull(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        RecoverItemDTO registerBoDTO = RecoverItemDTO.builder().location("123").build();
        Set<ConstraintViolation<RecoverItemDTO>> violations = validator.validate(registerBoDTO);

        assertFalse(violations.isEmpty());
    }

}