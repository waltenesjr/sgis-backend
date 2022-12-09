package br.com.oi.sgis.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RegisterBoDTOTest {

    private Validator validator;
    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void registerBoDTOTest(){
        RegisterBoDTO registerBoDTO = RegisterBoDTO.builder().build();
        registerBoDTO.setBoNumber("123");
        registerBoDTO.setUnityId("12");
        Set<ConstraintViolation<RegisterBoDTO>> violations = validator.validate(registerBoDTO);

        assertEquals("123", registerBoDTO.getBoNumber());
        assertEquals("12", registerBoDTO.getUnityId());
        assertTrue(violations.isEmpty());

    }

    @Test
    void registerBoDTOTestUnityIdNull(){
        RegisterBoDTO registerBoDTO = RegisterBoDTO.builder().build();
        registerBoDTO.setBoNumber("123");
        Set<ConstraintViolation<RegisterBoDTO>> violations = validator.validate(registerBoDTO);

        assertFalse(violations.isEmpty());
    }
}