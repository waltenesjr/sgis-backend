package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.AreaEquipamentDTO;
import br.com.oi.sgis.dto.ReprocessableUnityDTO;
import br.com.oi.sgis.dto.UnityToReprocessSapDTO;
import br.com.oi.sgis.enums.InstallationReasonEnum;
import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import br.com.oi.sgis.enums.RegisterReasonEnum;
import br.com.oi.sgis.enums.SapReprocessConditionEnum;
import br.com.oi.sgis.exception.AreaEquipamentNotFoundException;
import br.com.oi.sgis.exception.NotReprocessableUnityException;
import br.com.oi.sgis.util.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReprocessUnityService {

    private final AreaEquipamentService areaEquipamentService;

    public UnityToReprocessSapDTO reprocessUnity(ReprocessableUnityDTO reprocessableUnityDTO) throws NotReprocessableUnityException {
        isReprocessable(reprocessableUnityDTO);
        return reprocess(reprocessableUnityDTO);

    }

    @SneakyThrows
    private UnityToReprocessSapDTO reprocess(ReprocessableUnityDTO reprocessableUnityDTO) {
        switch (reprocessableUnityDTO.getCondition()) {
            case FIRST: return processFirstCondition(reprocessableUnityDTO);
            case SECOND: return processSecondCondition(reprocessableUnityDTO);
            case THIRD: return processThirdCondition(reprocessableUnityDTO);
            default: throw new NotReprocessableUnityException(MessageUtils.UNITY_REPROCESS_SAP_ERROR.getDescription());
        }
    }

    private UnityToReprocessSapDTO processThirdCondition(ReprocessableUnityDTO reprocessableUnityDTO) {
        UnityToReprocessSapDTO unityToReprocessSapDTO =  UnityToReprocessSapDTO.builder()
                .unityId(reprocessableUnityDTO.getUnityId())
                .condition(SapReprocessConditionEnum.THIRD.getCondition())
                .build();
        setAllInformations(reprocessableUnityDTO, unityToReprocessSapDTO);
        return unityToReprocessSapDTO;
    }

    private UnityToReprocessSapDTO processSecondCondition(ReprocessableUnityDTO reprocessableUnityDTO) {
        UnityToReprocessSapDTO unityToReprocessSapDTO =  UnityToReprocessSapDTO.builder()
                .unityId(reprocessableUnityDTO.getUnityId())
                .condition(SapReprocessConditionEnum.SECOND.getCondition())
                .build();
        setAllInformations(reprocessableUnityDTO, unityToReprocessSapDTO);
        unityToReprocessSapDTO.setOriginUf(reprocessableUnityDTO.getOriginUf());
        return unityToReprocessSapDTO;
    }

    @SneakyThrows
    private UnityToReprocessSapDTO processFirstCondition(ReprocessableUnityDTO reprocessableUnityDTO) {
        UnityToReprocessSapDTO unityToReprocessSapDTO =  UnityToReprocessSapDTO.builder()
                .unityId(reprocessableUnityDTO.getUnityId())
                .condition(SapReprocessConditionEnum.FIRST.getCondition())
                .build();
        setAllInformations(reprocessableUnityDTO, unityToReprocessSapDTO);
        return unityToReprocessSapDTO;
    }

    private void setAllInformations(ReprocessableUnityDTO reprocessableUnityDTO, UnityToReprocessSapDTO unityToReprocessSapDTO) {
        setBasicInformationToSapReprocess(reprocessableUnityDTO, unityToReprocessSapDTO);
        setOrderReservationAndFixedFields(reprocessableUnityDTO, unityToReprocessSapDTO);
        setReasonSapReprocess(reprocessableUnityDTO, unityToReprocessSapDTO);
    }

    @SneakyThrows
    private void setBasicInformationToSapReprocess(ReprocessableUnityDTO reprocessableUnityDTO, UnityToReprocessSapDTO unityToReprocessSapDTO){
        try {
            AreaEquipamentDTO unityModelDTO = areaEquipamentService.findById(reprocessableUnityDTO.getUnityModelId());
            unityToReprocessSapDTO.setAccountantCompany(reprocessableUnityDTO.getAccountantCompany());
            unityToReprocessSapDTO.setActiveClass(reprocessableUnityDTO.getActiveClass());
            unityToReprocessSapDTO.setResponsible(reprocessableUnityDTO.getResponsibleId());
            unityToReprocessSapDTO.setUnityCode(unityModelDTO.getId());
            unityToReprocessSapDTO.setMnemonic(unityModelDTO.getMnemonic());
        }catch (AreaEquipamentNotFoundException e ){
            throw new NotReprocessableUnityException(MessageUtils.UNITY_SAP_REPROCESS_UNITY_CODE_ERROR.getDescription());
        }
    }

    private void setOrderReservationAndFixedFields(ReprocessableUnityDTO reprocessableUnityDTO, UnityToReprocessSapDTO unityToReprocessSapDTO){
        unityToReprocessSapDTO.setSerieNumber(reprocessableUnityDTO.getSerieNumber());
        unityToReprocessSapDTO.setOrderItem(reprocessableUnityDTO.getOrderItem());
        unityToReprocessSapDTO.setOrderNumber(reprocessableUnityDTO.getOrderNumber());
        unityToReprocessSapDTO.setReservationItem(reprocessableUnityDTO.getReservationItem());
        unityToReprocessSapDTO.setReservationNumber(reprocessableUnityDTO.getReservationNumber());
        unityToReprocessSapDTO.setFixedNumber(reprocessableUnityDTO.getFixedNumber());
        unityToReprocessSapDTO.setFixedSubnumber(reprocessableUnityDTO.getFixedNumber());
    }

    private void setReasonSapReprocess(ReprocessableUnityDTO reprocessableUnityDTO, UnityToReprocessSapDTO unityToReprocessSapDTO) {
        unityToReprocessSapDTO.setReasonLabel("Motivo de Cadastro");
        if(reprocessableUnityDTO.getRegisterReason() != null){
            unityToReprocessSapDTO.setReason(RegisterReasonEnum.valueOf(reprocessableUnityDTO.getRegisterReason()).getDescription());
        }else if(reprocessableUnityDTO.getReasonForWriteOff()!= null) {
            setInstallationOrWriteOffReason(reprocessableUnityDTO, unityToReprocessSapDTO);
        } else {
            unityToReprocessSapDTO.setReason("");
        }
    }

    private void setInstallationOrWriteOffReason(ReprocessableUnityDTO reprocessableUnityDTO, UnityToReprocessSapDTO unityToReprocessSapDTO) {
        if(Arrays.stream(ReasonForWriteOffEnum.values()).map(ReasonForWriteOffEnum::getReason).collect(toList()).contains(reprocessableUnityDTO.getReasonForWriteOff())) {
            unityToReprocessSapDTO.setReason(ReasonForWriteOffEnum.getByReason(reprocessableUnityDTO.getReasonForWriteOff()).getDescription());
            unityToReprocessSapDTO.setReasonLabel("Motivo de Baixa");
        }
        else if(Arrays.stream(InstallationReasonEnum.values()).map(InstallationReasonEnum::getLostReason).collect(toList()).contains(reprocessableUnityDTO.getReasonForWriteOff())){
            unityToReprocessSapDTO.setReason(InstallationReasonEnum.getByLostReason(reprocessableUnityDTO.getReasonForWriteOff()).getDescription());
            unityToReprocessSapDTO.setReasonLabel("Motivo de Instalação");
        }
    }

    private void isReprocessable(ReprocessableUnityDTO reprocessableUnityDTO) throws NotReprocessableUnityException {
        if(reprocessableUnityDTO == null)
            throw new NotReprocessableUnityException(MessageUtils.UNITY_NOT_REPROCESSABLE_ERROR.getDescription());
        if(reprocessableUnityDTO.getCanBeReprocessed()==null || reprocessableUnityDTO.getCanBeReprocessed() == 3)
            throw new NotReprocessableUnityException(MessageUtils.UNITY_SAP_SUCCESS_ERROR.getDescription());
        if(reprocessableUnityDTO.getCanBeReprocessed() == 2)
            throw new NotReprocessableUnityException(MessageUtils.UNITY_ALREADY_REPROCESSABLE_ERROR.getDescription());
    }
}
