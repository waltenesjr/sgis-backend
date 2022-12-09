package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.MovItensReportDTO;
import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.exception.AreaEquipamentNotFoundException;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.exception.TechnicalStaffNotFoundException;
import br.com.oi.sgis.service.AreaEquipamentService;
import br.com.oi.sgis.service.DepartmentService;
import br.com.oi.sgis.service.SituationService;
import br.com.oi.sgis.service.TechnicalStaffService;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovItensReportValidator implements Validator<MovItensReportDTO> {

    private MovItensReportDTO movItensReportDTO;
    private final AreaEquipamentService areaEquipamentService;
    private final DepartmentService departmentService;
    private final TechnicalStaffService technicalStaffService;
    private final SituationService situationService;

    @Override
    public void validate(MovItensReportDTO movItensReportDTO) {
        this.movItensReportDTO = movItensReportDTO;
        validatePeriod();
        validateUnityCode();
        validateFromResponsible();
        validateToResponsible();
        validateFromTechnician();
        validateToTechnician();
        validateToSituations();
        validateFromSituations();
    }

    private void validateToSituations() {
        if(movItensReportDTO.getToSituations()==null)
            return;
        List<SituationDTO> allSituations = situationService.listAll();
        if(!allSituations.stream().map(SituationDTO::getId).collect(Collectors.toList()).containsAll(movItensReportDTO.getToSituations())){
            throw new IllegalArgumentException(MessageUtils.INVALID_DESTINY_SITUATION.getDescription());
        }
    }

    private void validateFromSituations() {
        if(movItensReportDTO.getFromSituations()==null)
            return;
        List<SituationDTO> allSituations = situationService.listAll();
        if(!allSituations.stream().map(SituationDTO::getId).collect(Collectors.toList()).containsAll(movItensReportDTO.getFromSituations())){
            throw new IllegalArgumentException(MessageUtils.INVALID_ORIGIN_SITUATION.getDescription());
        }
    }

    private void validateToResponsible() {
        if(movItensReportDTO.getToResponsible()==null)
            return;
        try {
            departmentService.findById(movItensReportDTO.getToResponsible());
        }catch (DepartmentNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.INVALID_DESTINY_RESPONSIBLE.getDescription());
        }
    }

    private void validateFromResponsible() {
        if(movItensReportDTO.getFromResponsible()==null)
            return;
        try {
            departmentService.findById(movItensReportDTO.getFromResponsible());
        }catch (DepartmentNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.INVALID_ORIGIN_RESPONSIBLE.getDescription());
        }
    }

    private void validateToTechnician() {
        if(movItensReportDTO.getToTechnician()==null)
            return;
        try {
            technicalStaffService.findById(movItensReportDTO.getToTechnician());
        }catch (TechnicalStaffNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.INVALID_DESTINY_TECHNICIAN.getDescription());
        }
    }

    private void validateFromTechnician() {
        if(movItensReportDTO.getFromTechnician()==null)
            return;
        try {
            technicalStaffService.findById(movItensReportDTO.getFromTechnician());
        }catch (TechnicalStaffNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.INVALID_ORIGIN_TECHNICIAN.getDescription());
        }
    }

    private void validateUnityCode() {
        if(movItensReportDTO.getUnityCode()==null)
            return;
        try {
            areaEquipamentService.findById(movItensReportDTO.getUnityCode());
        }catch (AreaEquipamentNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.AREA_EQUIPAMENT_INVALID.getDescription());
        }
    }

    private void validatePeriod() {
        if (movItensReportDTO.getInitialPeriod().isAfter(movItensReportDTO.getEndPeriod())){
           throw new IllegalArgumentException(MessageUtils.INVALID_PERIOD.getDescription());
        }
    }
}
