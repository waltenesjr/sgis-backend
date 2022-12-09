package br.com.oi.sgis.service.validator;

import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.UnityException;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewSpareUnityValidator  {


    private Unity unity;

    @SneakyThrows
    public void validateNewSpareUnity(Unity unityToValidate) {
        unity = unityToValidate;
        validateRequiredDatasFilled();
        if(!validateSetOfRequiredDatas()){
            throw new UnityException(MessageUtils.UNITY_INVALID_REQUIRED_SET.getDescription());
        }

    }

    private boolean validateSetOfRequiredDatas()  {
        return validateFixedSetRequired() || validateOrderSetRequired() || validateResevationSetRequired();
    }

    private void validateRequiredDatasFilled() throws UnityException {
        if(!unity.isSomeFixedSetFilled() && !unity.isSomeResevationSetFilled() && !unity.isSomeOrderSetFilled())
            throw new UnityException(MessageUtils.UNITY_NONE_REQUIRED_SET.getDescription());
    }

    private boolean validateResevationSetRequired(){
        return !unity.isSomeOrderSetFilled() && !unity.isSomeFixedSetFilled() && unity.isResevationSetTotallyFilled();
    }
    private boolean validateFixedSetRequired(){
        return !unity.isSomeResevationSetFilled() && !unity.isSomeOrderSetFilled() && unity.isFixedSetTotallyFilled();
    }
    private boolean validateOrderSetRequired(){
        return !unity.isSomeResevationSetFilled() && !unity.isSomeFixedSetFilled() && unity.isOrderSetTotallyFilled();
    }
}
