package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.UnitySwapDTO;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.repository.AreaEquipamentRepository;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitySwapValidator implements Validator<UnitySwapDTO> {

    private UnitySwapDTO unitySwapDTO;

    private final UnityRepository unityRepository;
    private final AreaEquipamentRepository areaEquipamentRepository;

    @Override
    public void validate(UnitySwapDTO unitySwapDTO) {
        this.unitySwapDTO = unitySwapDTO;

        validateBarCode();
        validateItemToSwap();
        validateNewUnityCode();

    }

    private void validateBarCode() {
        if(unitySwapDTO.getUnityId().trim().equals(unitySwapDTO.getUnityNewBarcode().trim()))
            throw new IllegalArgumentException(MessageUtils.UNITY_SWAP_BARCODE_EQ_ORIGINAL_ERROR.getDescription());

        if(unityRepository.findById(unitySwapDTO.getUnityNewBarcode()).isPresent())
            throw new IllegalArgumentException(MessageUtils.UNITY_ALREADY_SAVED.getDescription() + unitySwapDTO.getUnityNewBarcode());
    }

    private void validateItemToSwap(){
       Unity unity = unityRepository.findById(unitySwapDTO.getUnityId())
                .orElseThrow(() -> new IllegalArgumentException(MessageUtils.INVALID_UNITY_CODE_ERROR.getDescription()));

       validateSituation(unity);
       validateResponsible(unity);
    }

    private void validateResponsible(Unity unity) {
        if(!unity.getResponsible().getId().equals(Utils.getUser().getDepartmentCode().getId())){
            throw new IllegalArgumentException(MessageUtils.UNITY_SWAP_INVALID_RESPONSIBLE_ERROR.getDescription());
        }
    }

    private void validateSituation(Unity unity) {
        List<String> situationsAllowed = List.of(SituationEnum.DIS.getCod(), SituationEnum.DEF.getCod());
        if(!situationsAllowed.contains(unity.getSituationCode().getId())){
            throw new IllegalArgumentException(MessageUtils.UNITY_SWAP_INVALID_SITUATION_ERROR.getDescription());
        }
    }

    private void validateNewUnityCode() {
            areaEquipamentRepository.findById(unitySwapDTO.getNewUnityCode()).orElseThrow(
                    ()-> new IllegalArgumentException(MessageUtils.UNITY_SWAP_INVALID_NEW_UNITYCODE_ERROR.getDescription())
            );
    }

}
