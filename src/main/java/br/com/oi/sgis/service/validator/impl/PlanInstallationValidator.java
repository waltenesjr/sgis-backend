package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.PlanInstallationDTO;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.InstallationReasonEnum;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.repository.TechnicalStaffRepository;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.StationService;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanInstallationValidator implements Validator<PlanInstallationDTO> {

    private PlanInstallationDTO planInstallationDTO;
    private final UnityRepository unityRepository;
    private final StationService stationService;
    private final TechnicalStaffRepository technicalStaffRepository;

    @Override
    public void validate(PlanInstallationDTO planInstallationDTO) {
        this.planInstallationDTO = planInstallationDTO;
        validateUnity();
        validateStation();
        validateTechnician();
        validateSinisterNumber();
        validateBoNumber();
    }

    private void validateBoNumber() {
        if(planInstallationDTO.getBoNumber() == null &&
                (InstallationReasonEnum.incidentsAndBurning().contains(planInstallationDTO.getInstallationReason()) ||
                        InstallationReasonEnum.steals().contains(planInstallationDTO.getInstallationReason()))){
            throw new IllegalArgumentException(MessageUtils.INVALID_BO_NUMBER_ERROR.getDescription());
        }
    }

    private void validateSinisterNumber() {
        if(planInstallationDTO.getSinisterNumber() == null &&
                InstallationReasonEnum.incidentsAndBurning().contains(planInstallationDTO.getInstallationReason())){
            throw new IllegalArgumentException(MessageUtils.INVALID_SINISTER_NUMBER_ERROR.getDescription());
        }

    }

    private Unity validateUnity() {
        return unityRepository.findById(planInstallationDTO.getUnityId())
                .orElseThrow(() -> new IllegalArgumentException(MessageUtils.INVALID_UNITY_CODE_ERROR.getDescription()));
    }

    @SneakyThrows
    private void validateStation() {
        try{
            stationService.findById(planInstallationDTO.getStationId());
        }catch (StationNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.STATION_INVALID.getDescription());
        }
    }
    private void validateTechnician() {
        technicalStaffRepository.findById(planInstallationDTO.getTechnicianId()).orElseThrow(
                ()-> new IllegalArgumentException(MessageUtils.TECHNICAL_STAFF_INVALID.getDescription())
        );
    }

}
