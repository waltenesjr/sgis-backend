package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.UnityWriteOffDTO;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.MessageUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UnityWriteOffValidatorTest {

    @InjectMocks
    private UnityWriteOffValidator unityWriteOffValidator;
    @Mock
    private UnityRepository unityRepository;

    private UnityWriteOffDTO writeOffDTO;

    private Unity unity;

    @BeforeEach
    void setUp(){
        writeOffDTO = new EasyRandom().nextObject(UnityWriteOffDTO.class);
        unity =  new EasyRandom().nextObject(Unity.class);
        unity.setResponsible(Department.builder().id("AAAAA").build());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
    }

    @Test
    void validate() {
        assertDoesNotThrow(()->unityWriteOffValidator.validate(writeOffDTO));
    }

    @Test
    void invalidReasonAccountantCompany() {
        unity.setAccountantCompany(null);
        writeOffDTO.setReasonForWriteOff(ReasonForWriteOffEnum.OBS);
        Exception exception =  assertThrows(IllegalArgumentException.class, ()->unityWriteOffValidator.validate(writeOffDTO));
        assertEquals(MessageUtils.UNITY_WRITE_OFF_ACC_COMP_ERROR.getDescription(), exception.getMessage());
    }

    @Test
    void invalidTechnicalReport() {
        writeOffDTO.setTechnicalReport("");
        writeOffDTO.setReasonForWriteOff(ReasonForWriteOffEnum.OBS);

        Exception exception =  assertThrows(IllegalArgumentException.class, ()->unityWriteOffValidator.validate(writeOffDTO));
        assertEquals(MessageUtils.UNITY_WRITE_OFF_TECH_REPORT_ERROR.getDescription(), exception.getMessage());
    }

}