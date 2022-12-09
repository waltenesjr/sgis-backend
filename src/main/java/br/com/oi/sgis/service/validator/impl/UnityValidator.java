package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.RegisterReasonEnum;
import br.com.oi.sgis.exception.*;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.*;
import br.com.oi.sgis.service.validator.NewSpareUnityValidator;
import br.com.oi.sgis.service.validator.UnityRemovedFromSiteValidator;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnityValidator implements Validator<Unity> {

    private final UnityRepository unityRepository;
    private final DepartmentService departmentService;
    private final AreaEquipamentService areaEquipamentService;
    private final TechnicalStaffService technicalStaffService;
    private final StationService stationService;
    private final FiscalDocumentService fiscalDocumentService;

    private Unity unity;
    private final UnityRemovedFromSiteValidator unityRemovedFromSiteValidator;
    private final NewSpareUnityValidator newSpareUnityValidator;

    @SneakyThrows
    @Override
    public void validate(Unity unityToValidate) {
        unity = unityToValidate;
        validateBarCode();
        validateUnityCode();
        validateResponsible();
        validateTechnician();
        validateDeposit();
        validateStation();
        validateFiscalDocument();
        validadeByRegisterReason();

    }

    @SneakyThrows
    private void validateFiscalDocument() {
        if(unity.getFiscalDocument()==null)
            return;

        try {
            fiscalDocumentService.findById(unity.getFiscalDocument().getId());
        }catch (FiscalDocumentNotFoundException e){
            throw new UnityException(MessageUtils.UNITY_SAVED_ERROR.getDescription() + ": "+ e.getMessage() );
        }
    }

    @SneakyThrows
    private void validateStation() {
        if(unity.getStation() == null)
            return;
        try {
            stationService.findById(unity.getStation().getId());
        }catch (StationNotFoundException e){
            throw new UnityException(MessageUtils.UNITY_SAVED_ERROR.getDescription() + ": "+ e.getMessage() );
        }
    }

    @SneakyThrows
    private void validateDeposit()  {
        if(unity.getDeposit()==null)
            throw new UnityException(MessageUtils.DEPARTMENT_NOT_FOUND_BY_ID.getDescription());

        try {
            departmentService.findById(unity.getDeposit().getId());
        } catch (DepartmentNotFoundException e) {
            throw new UnityException(MessageUtils.UNITY_SAVED_ERROR.getDescription() + ": " + e.getMessage());
        }
    }

    @SneakyThrows
    private void validateTechnician() {
        if(unity.getTechnician()==null)
            throw new UnityException(MessageUtils.TECHNICAL_STAFF_INVALID.getDescription());

        try {
            technicalStaffService.findById(unity.getTechnician().getId());
        } catch (TechnicalStaffNotFoundException e) {
            throw new UnityException(MessageUtils.TECHNICAL_STAFF_INVALID.getDescription() + ": " + e.getMessage());
        }

    }

    private void validadeByRegisterReason() {
        if(unity.getRegisterReason().equals(RegisterReasonEnum.CUS.getReason())) {
            newSpareUnityValidator.validateNewSpareUnity(unity);
        }else {
            unityRemovedFromSiteValidator.validateUnityRemovedFromSite(unity);
        }
    }

    @SneakyThrows
    private void validateBarCode() {
        if(unityRepository.findById(unity.getId()).isPresent()){
            throw new UnityException(MessageUtils.UNITY_ALREADY_SAVED.getDescription() + unity.getId());
        }
    }

    @SneakyThrows
    private void validateResponsible() {
        if (unity.getResponsible() != null) {
            try {
                departmentService.findById(unity.getResponsible().getId());
            } catch (DepartmentNotFoundException e) {
                throw new UnityException(MessageUtils.UNITY_SAVED_ERROR.getDescription() + ": " + e.getMessage());
            }
        }
    }

    @SneakyThrows
    private void validateUnityCode() {
        if (unity.getUnityCode() == null)
            throw new UnityException(MessageUtils.INVALID_UNITY_CODE_ERROR.getDescription());

        try {
            areaEquipamentService.findById(unity.getUnityCode().getId());
        } catch (AreaEquipamentNotFoundException e) {
            throw new UnityException(MessageUtils.INVALID_UNITY_CODE_ERROR.getDescription() + ": " + e.getMessage());
        }
    }

}
