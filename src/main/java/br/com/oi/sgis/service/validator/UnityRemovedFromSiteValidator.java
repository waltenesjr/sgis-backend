package br.com.oi.sgis.service.validator;

import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.exception.UnityException;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnityRemovedFromSiteValidator  {

    private Unity unity;

    @SneakyThrows
    public void validateUnityRemovedFromSite(Unity unityToValidate) {
        unity = unityToValidate;
        validateSituation();
    }

    private void validateSituation() throws UnityException {
        if(unity.getSituationCode() == null)
            throw new UnityException(MessageUtils.UNITY_INVALID_SITUATION.getDescription());

        String situationCode = unity.getSituationCode().getId();
        if(!(situationCode.equals(SituationEnum.DIS.getCod())|| situationCode.equals(SituationEnum.DEF.getCod())))
            throw new UnityException(MessageUtils.UNITY_INVALID_SITUATION.getDescription());

    }

}
