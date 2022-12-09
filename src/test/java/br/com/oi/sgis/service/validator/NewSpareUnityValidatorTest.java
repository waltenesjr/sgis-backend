package br.com.oi.sgis.service.validator;

import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.UnityException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class NewSpareUnityValidatorTest {

    @InjectMocks @Spy
    private NewSpareUnityValidator newSpareUnityValidator;

    @Test
    void shouldValidateNewSpareUnityByReservationSet(){
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        unityToValidate.setFixedNumber(null);
        unityToValidate.setFixedSubnumber(null);
        unityToValidate.setOrderNumber(null);
        unityToValidate.setOrderItem(null);

        Assertions.assertDoesNotThrow(()->newSpareUnityValidator.validateNewSpareUnity(unityToValidate));
    }

    @Test
    void shouldValidateNewSpareUnityByFixedSet(){
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        unityToValidate.setReservationNumber(null);
        unityToValidate.setReservationItem(null);
        unityToValidate.setOrderNumber(null);
        unityToValidate.setOrderItem(null);

        Assertions.assertDoesNotThrow(()->newSpareUnityValidator.validateNewSpareUnity(unityToValidate));
    }

    @Test
    void shouldValidateNewSpareUnityByOrderSet(){
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        unityToValidate.setReservationNumber(null);
        unityToValidate.setReservationItem(null);
        unityToValidate.setFixedNumber(null);
        unityToValidate.setFixedSubnumber(null);

        Assertions.assertDoesNotThrow(()->newSpareUnityValidator.validateNewSpareUnity(unityToValidate));
    }

    @Test
    void shouldThrowExceptionNoneRequiredData(){
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        unityToValidate.setReservationNumber(null);
        unityToValidate.setReservationItem(null);
        unityToValidate.setFixedNumber(null);
        unityToValidate.setFixedSubnumber(null);
        unityToValidate.setOrderNumber(null);
        unityToValidate.setOrderItem(null);
        Assertions.assertThrows(UnityException.class, ()->newSpareUnityValidator.validateNewSpareUnity(unityToValidate));
    }

    @Test
    void shouldThrowExceptionMoreThanOneRequiredSetData(){
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);
        unityToValidate.setReservationItem(null);
        unityToValidate.setFixedNumber(null);
        unityToValidate.setFixedSubnumber(null);
        Assertions.assertThrows(UnityException.class, ()->newSpareUnityValidator.validateNewSpareUnity(unityToValidate));
    }
}