package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.UnitySwapDTO;
import br.com.oi.sgis.entity.AreaEquipament;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Situation;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.repository.AreaEquipamentRepository;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.Utils;
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
class UnitySwapValidatorTest {

    @InjectMocks
    private UnitySwapValidator unitySwapValidator;

    @Mock
    private UnityRepository unityRepository;
    @Mock
    private AreaEquipamentRepository areaEquipamentRepository;

    private UnitySwapDTO unitySwapDTO;

    private Unity unity;

    @BeforeEach
    void setUp(){
        unitySwapDTO = new EasyRandom().nextObject(UnitySwapDTO.class);
        unity = new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        unity.setResponsible(Department.builder().id(Utils.getUser().getDepartmentCode().getId()).build());

    }

    @Test
    void shouldValidate(){
        Mockito.when(unityRepository.findById(Mockito.any())).thenAnswer(answer->{
            if(answer.getArguments()[0].equals(unitySwapDTO.getUnityId()))
                return Optional.of(unity);
            return Optional.empty();
        });
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        Mockito.doReturn(Optional.of(areaEquipament)).when(areaEquipamentRepository).findById(Mockito.any());

        assertDoesNotThrow(()->unitySwapValidator.validate(unitySwapDTO));
    }

    @Test
    void shouldNotValidateSituation(){
        Mockito.when(unityRepository.findById(Mockito.any())).thenAnswer(answer->{
            if(answer.getArguments()[0].equals(unitySwapDTO.getUnityId()))
                return Optional.of(unity);
            return Optional.empty();
        });
        unity.setSituationCode(Situation.builder().id("OFE").build());
        Exception e = assertThrows(IllegalArgumentException.class, ()->unitySwapValidator.validate(unitySwapDTO));
        assertEquals("Swap não permitido para unidades cujas situações não se encontram em DIS ou DEF.", e.getMessage());
    }

    @Test
    void shouldNotValidateResponsible(){
        Mockito.when(unityRepository.findById(Mockito.any())).thenAnswer(answer->{
            if(answer.getArguments()[0].equals(unitySwapDTO.getUnityId()))
                return Optional.of(unity);
            return Optional.empty();
        });
        unity.setResponsible(Department.builder().id("Test").build());
        Exception e = assertThrows(IllegalArgumentException.class, ()->unitySwapValidator.validate(unitySwapDTO));
        assertEquals("Este item não é da sua área administrativa.", e.getMessage());
    }

    @Test
    void shouldNotValidateNewUnityCode() {
        Mockito.when(unityRepository.findById(Mockito.any())).thenAnswer(answer->{
            if(answer.getArguments()[0].equals(unitySwapDTO.getUnityId()))
                return Optional.of(unity);
            return Optional.empty();
        });
        Mockito.doReturn(Optional.empty()).when(areaEquipamentRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->unitySwapValidator.validate(unitySwapDTO));
        assertEquals("Novo código da unidade não cadastrado.", e.getMessage());
    }
    @Test
    void shouldNotValidateSameBarcode() {
        unitySwapDTO.setUnityNewBarcode(unitySwapDTO.getUnityId());
        Exception e = assertThrows(IllegalArgumentException.class, ()->unitySwapValidator.validate(unitySwapDTO));
        assertEquals("Novo Código de barras não pode ser igual ao original.", e.getMessage());
    }
    @Test
    void shouldNotValidateBarcodeAlreadyExists() {
        Mockito.doReturn( Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->unitySwapValidator.validate(unitySwapDTO));
        assertEquals("Unidade já se encontra cadastrada com cód. barras, " + unitySwapDTO.getUnityNewBarcode(), e.getMessage());
    }



}