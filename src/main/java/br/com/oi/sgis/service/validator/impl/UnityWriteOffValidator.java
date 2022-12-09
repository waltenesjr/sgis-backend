package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.UnityWriteOffDTO;
import br.com.oi.sgis.entity.Level;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnityWriteOffValidator implements Validator<UnityWriteOffDTO> {

    private final UnityRepository unityRepository;
    private UnityWriteOffDTO unityWriteOffDTO;
    private Unity unity;

    @Override
    public void validate(UnityWriteOffDTO writeOffDTO) {
        unityWriteOffDTO = writeOffDTO;
        validateUnity();
        validateReason();
    }

    private void validateUnity() {
        unity = unityRepository.findById(unityWriteOffDTO.getUnityId())
                .orElseThrow(() -> new IllegalArgumentException(MessageUtils.INVALID_UNITY_CODE_ERROR.getDescription()));
        validateUserPermission(unity);
    }

    private void validateUserPermission(Unity unity) {
        List<Integer> userLevels = Utils.getUser().getLevels().stream().map(Level::getLvl).collect(Collectors.toList());
        String userDepartment = Utils.getUser().getDepartmentCode().getId();
        if(!userLevels.contains(0) && !unity.getResponsible().getId().equals(userDepartment)){
            throw new IllegalArgumentException(String.format(MessageUtils.UNITY_WRITE_OFF_PERMISION_ERROR.getDescription(),Utils.getUser().getId(), unity.getId()));
        }
    }

    private void validateReason() {
        if(ReasonForWriteOffEnum.needsTechnicalReport().contains(unityWriteOffDTO.getReasonForWriteOff())){
            validateAccountantCompany();
            validateTechnicalReport();
        }
    }

    private void validateTechnicalReport() {
        if(unityWriteOffDTO.getTechnicalReport() == null || unityWriteOffDTO.getTechnicalReport().isBlank())
            throw new IllegalArgumentException(MessageUtils.UNITY_WRITE_OFF_TECH_REPORT_ERROR.getDescription());
    }

    private void validateAccountantCompany() {
        if(unity.getAccountantCompany() == null)
            throw new IllegalArgumentException(MessageUtils.UNITY_WRITE_OFF_ACC_COMP_ERROR.getDescription());
    }
}
