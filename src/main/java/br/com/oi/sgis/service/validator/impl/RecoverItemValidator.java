package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.RecoverItemDTO;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.StationService;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecoverItemValidator implements Validator<RecoverItemDTO> {

    private RecoverItemDTO recoverItemDTO;
    private final StationService stationService;
    private final UnityRepository unityRepository;


    @Override
    public void validate(RecoverItemDTO recoverItemDTO) {
        this.recoverItemDTO = recoverItemDTO;
        validateUnity();
        validateStation();
    }

    @SneakyThrows
    private void validateStation() {
        try {
            if (recoverItemDTO.getStationId() != null)
                stationService.findById(recoverItemDTO.getStationId());
        } catch (StationNotFoundException e) {
            throw new IllegalArgumentException(MessageUtils.STATION_INVALID.getDescription());
        }
    }

    private void validateUnity() {
        Unity unity =  unityRepository.findById(recoverItemDTO.getUnityId())
                .orElseThrow(() -> new IllegalArgumentException(MessageUtils.INVALID_UNITY_CODE_ERROR.getDescription()));
        validateUnitySituation(unity);
        validateSapStatus(unity);
        validateUserPermission(unity);
    }

    private void validateSapStatus(Unity unity) {
        if(unity.getSapStatus() == null || !unity.getSapStatus().equals("1"))
            throw new IllegalArgumentException(MessageUtils.UNITY_RECOVER_SAP_STATUS_ERROR.getDescription());
    }

    private void validateUserPermission(Unity unity) {
        String userDepartment = Utils.getUser().getDepartmentCode().getId();
        if(!unity.getResponsible().getId().equals(userDepartment)){
            throw new IllegalArgumentException(String.format(MessageUtils.UNITY_DIFFERENT_AREA_ERROR.getDescription(),Utils.getUser().getId(), unity.getId()));
        }
    }

    private void validateUnitySituation(Unity unity) {
        List<String> situations = List.of("BXU", "BXE", "BXO", "BXP");
        if(unity.getSituationCode()==null || !situations.contains(unity.getSituationCode().getId()))
            throw new IllegalArgumentException(MessageUtils.UNITY_RECOVER_INVALID_SITUATION_ERROR.getDescription());
    }

}
