package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.SummaryItemCriteriaReportDTO;
import br.com.oi.sgis.exception.*;
import br.com.oi.sgis.service.*;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummaryItemReportValidator implements Validator<SummaryItemCriteriaReportDTO> {

    private SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO;
    private final StationService stationService;
    private final DepartmentService departmentService;
    private final CompanyService companyService;
    private final ApplicationService applicationService;
    private final AreaEquipamentService areaEquipamentService;
    private final EquipamentTypeService equipamentTypeService;
    private final ModelEquipTypeService modelEquipTypeService;


    @Override
    public void validate(SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO) {
        this.summaryItemCriteriaReportDTO = summaryItemCriteriaReportDTO;
        validateApplication();
        validateType();
        validateModel();
        validateUnityCode();
        validateMnemonic();
        validateCompany();
        validateResponsible();
        validateStation();
    }

    private void validateApplication(){
        try {
            if(summaryItemCriteriaReportDTO.getApplicationCode()!=null)
                applicationService.findById(summaryItemCriteriaReportDTO.getApplicationCode());
        }catch (ApplicationNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.APPLICATION_IVALID.getDescription());
        }
    }

    private void validateType(){
        try {
            if(summaryItemCriteriaReportDTO.getTypeCode() != null)
                equipamentTypeService.findById(summaryItemCriteriaReportDTO.getTypeCode());
        }catch (EquipamentTypeNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.EQUIPAMENT_TYPE_INVALID.getDescription());
        }
    }

    private void validateModel(){
        try{
            if(summaryItemCriteriaReportDTO.getModelCode() != null)
                modelEquipTypeService.findById(summaryItemCriteriaReportDTO.getModelCode());
        }catch (ModelEquipTypeNotFound e){
            throw new IllegalArgumentException(MessageUtils.MODEL_EQUIPAMENT_TYPE_INVALID.getDescription());
        }
    }

    private void validateUnityCode(){
        try {
            if(summaryItemCriteriaReportDTO.getUnityCode()!=null)
                areaEquipamentService.findById(summaryItemCriteriaReportDTO.getUnityCode());
        }catch (AreaEquipamentNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.AREA_EQUIPAMENT_INVALID.getDescription());
        }

    }

    private void validateMnemonic() {
        try {
            if(summaryItemCriteriaReportDTO.getMnemonic()!=null)
                areaEquipamentService.findByMnemonic(summaryItemCriteriaReportDTO.getMnemonic());
        }catch (AreaEquipamentNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.AREA_EQUIPAMENT_INVALID_MNEMONIC.getDescription());
        }
    }

    private void validateCompany() {
        try {
            if(summaryItemCriteriaReportDTO.getCompanyCode()!=null)
                companyService.findById(summaryItemCriteriaReportDTO.getCompanyCode());
        }catch (CompanyNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.COMPANY_INVALID.getDescription());
        }
    }

    private void validateResponsible() {
        try {
            if(summaryItemCriteriaReportDTO.getResponsibleCode()!=null)
                departmentService.findById(summaryItemCriteriaReportDTO.getResponsibleCode());
        } catch (DepartmentNotFoundException e) {
            throw new IllegalArgumentException(MessageUtils.DEPARTMENT_RESPONSIBLE_INVALID.getDescription());
        }
    }

    private void validateStation() {
        try{
            if(summaryItemCriteriaReportDTO.getStationCode() != null)
                stationService.findById(summaryItemCriteriaReportDTO.getStationCode());
        }catch (StationNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.STATION_INVALID.getDescription());
        }
    }

}
