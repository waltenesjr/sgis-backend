package br.com.oi.sgis.service.validator;

import br.com.oi.sgis.entity.Situation;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.exception.UnityException;
import br.com.oi.sgis.util.MessageUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnityRemovedFromSiteValidatorTest {

    @InjectMocks @Spy
    private UnityRemovedFromSiteValidator unityRemovedFromSiteValidator;

    @Test
    void shouldValidateRemovedFromSite(){
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);

        unityToValidate.setSituationCode(Situation.builder().id(SituationEnum.DIS.getCod()).build());

        Assertions.assertDoesNotThrow(()-> unityRemovedFromSiteValidator.validateUnityRemovedFromSite(unityToValidate));
    }

    @Test
    void shouldThowExceptionValidateRemovedFromSite(){
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);

        unityToValidate.setSituationCode(Situation.builder().id(SituationEnum.TRN.getCod()).build());
        Assertions.assertThrows(UnityException.class, ()-> unityRemovedFromSiteValidator.validateUnityRemovedFromSite(unityToValidate));
    }
    @Test
    void shouldThowExceptionValidateRemovedFromSiteSituationNull(){
        Unity unityToValidate = new EasyRandom().nextObject(Unity.class);

        unityToValidate.setSituationCode(null);
        Exception e = Assertions.assertThrows(UnityException.class, ()-> unityRemovedFromSiteValidator.validateUnityRemovedFromSite(unityToValidate));
        Assertions.assertEquals(MessageUtils.UNITY_INVALID_SITUATION.getDescription(), e.getMessage());
    }

}