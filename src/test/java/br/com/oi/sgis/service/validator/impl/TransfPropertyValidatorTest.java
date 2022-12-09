package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.TransfPropertyDTO;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.DepartmentService;
import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class TransfPropertyValidatorTest {

    @InjectMocks
    private TransfPropertyValidator transfPropertyValidator;

    @Mock
    private DepartmentService departmentService;
    @Mock
    private UnityRepository unityRepository;

    @Test @SneakyThrows
    void shouldValidate(){
        TransfPropertyDTO transfPropertyDTO = new EasyRandom().nextObject(TransfPropertyDTO.class);
        Unity unity = new EasyRandom().nextObject(Unity.class);
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);

        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());

        Assertions.assertDoesNotThrow(()->transfPropertyValidator.validate(transfPropertyDTO));
    }

    @Test @SneakyThrows
    void shouldThrowExceptionDestinationEqualsDeposit(){
        TransfPropertyDTO transfPropertyDTO = new EasyRandom().nextObject(TransfPropertyDTO.class);
        Unity unity = new EasyRandom().nextObject(Unity.class);
        transfPropertyDTO.setIdDepDestination(unity.getDeposit().getId());
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);

        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());

        Assertions.assertThrows(IllegalArgumentException.class, ()->transfPropertyValidator.validate(transfPropertyDTO));
    }

    @Test @SneakyThrows
    void shouldThrowExceptionDestinationNull(){
        TransfPropertyDTO transfPropertyDTO = new EasyRandom().nextObject(TransfPropertyDTO.class);
        transfPropertyDTO.setIdDepDestination(null);

        Assertions.assertThrows(IllegalArgumentException.class, ()->transfPropertyValidator.validate(transfPropertyDTO));
    }

    @Test @SneakyThrows
    void shouldThrowExceptionDestinationNotFound(){
        TransfPropertyDTO transfPropertyDTO = new EasyRandom().nextObject(TransfPropertyDTO.class);
        Mockito.doThrow(DepartmentNotFoundException.class).when(departmentService).findById(Mockito.any());

        Assertions.assertThrows(IllegalArgumentException.class, ()->transfPropertyValidator.validate(transfPropertyDTO));
    }
}