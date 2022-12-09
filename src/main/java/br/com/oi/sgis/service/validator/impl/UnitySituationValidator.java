package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.dto.UnitySituationDTO;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.DepartmentService;
import br.com.oi.sgis.service.SituationService;
import br.com.oi.sgis.service.StationService;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitySituationValidator implements Validator<UnitySituationDTO> {

    private Unity unity;
    private UnitySituationDTO unitySituationDTO;

    private final UnityRepository unityRepository;
    private final StationService stationService;
    private final DepartmentService departmentService;
    private final SituationService situationService;

    @Override
    public void validate(UnitySituationDTO unitySituationDTO) {
        this.unitySituationDTO = unitySituationDTO;

        validateUnity();
        validateUpdateToSameSituation();
        validateSituationAllowed(unitySituationDTO.getSituation());
        validateResevation();
        validateStation();
    }

    private void validateUnity() {
        unity = unityRepository.findById(unitySituationDTO.getUnityId())
            .orElseThrow(() -> new IllegalArgumentException(MessageUtils.INVALID_UNITY_CODE_ERROR.getDescription()));
    }

    private void validateUpdateToSameSituation() {
        if(unity.getSituationCode().getId().equals(unitySituationDTO.getSituation().getCod()))
                throw new IllegalArgumentException(MessageUtils.UNITY_SITUATION_SAME_SIT_ERROR.getDescription()
                        + unitySituationDTO.getSituation().getDescription());
    }

    @SneakyThrows
    private void validateStation() {
        if(unitySituationDTO.getStationId() == null)
            return;
        try{
            stationService.findById(unitySituationDTO.getStationId());
        }catch (StationNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.STATION_INVALID.getDescription());
        }
    }

    private void validateResevation() {
        if(!unitySituationDTO.getSituation().equals(SituationEnum.RES))
            return;

        if(unitySituationDTO.getReservationId() == null)
            throw new IllegalArgumentException(MessageUtils.UNITY_SITUATION_NULL_RESERVATION_ERROR.getDescription());

        if(!unity.getSituationCode().getId().equals(SituationEnum.DIS.getCod()))
            throw new IllegalArgumentException(MessageUtils.UNITY_SITUATION_RESERVATION_ERROR.getDescription());
        try{
            departmentService.findById(unitySituationDTO.getReservationId());
        }catch (DepartmentNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_SITUATION_INVALID_RESERVATION.getDescription());
        }
    }

    private void validateSituationAllowed(SituationEnum situation) {
        List<String> codSituationsAllowed = situationService.listAllToUpdateUnity()
                .stream().map(SituationDTO::getId).collect(Collectors.toList());
        if(!codSituationsAllowed.contains(situation.getCod())){
            throw new IllegalArgumentException(MessageUtils.UNITY_SITUATION_INVALID_SITUATION_ERROR.getDescription());
        }
        if((!situation.equals(SituationEnum.DIS)) && codSituationsAllowed.contains(unity.getSituationCode().getId()) ){
            throw new IllegalArgumentException(String.format(MessageUtils.UNITY_SITUATION_ONLY_DIS_ALLOWED_ERROR.getDescription(), unity.getSituationCode().getDescription()));
        }
    }
}
