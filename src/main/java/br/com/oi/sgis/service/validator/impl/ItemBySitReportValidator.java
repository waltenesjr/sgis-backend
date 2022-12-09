package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.ItemBySitReportCriteriaDTO;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.service.DepartmentService;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ItemBySitReportValidator implements Validator<ItemBySitReportCriteriaDTO> {

    private ItemBySitReportCriteriaDTO criteriaDTO;
    private final DepartmentService departmentService;


    @Override
    public void validate(ItemBySitReportCriteriaDTO criteriaDTO) {
        this.criteriaDTO = criteriaDTO;
        validateMovDates();
        validateRegDates();
        validateDepartment();
    }

    private void validateDepartment() {
        try {
            if(criteriaDTO.getDepartment()!=null)
                departmentService.findById(criteriaDTO.getDepartment());
        }catch (DepartmentNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.DEPARTMENT_INVALID.getDescription());
        }
    }

    private void validateRegDates() {
        if(criteriaDTO.getFinalRegDate()==null && criteriaDTO.getInitialRegDate()==null)
            return;
        if(isPeriodInvalid(criteriaDTO.getFinalRegDate(),criteriaDTO.getInitialRegDate()))
            throw new IllegalArgumentException(MessageUtils.INVALID_REGISTER_PERIOD.getDescription());
        if (criteriaDTO.getInitialRegDate().isAfter(criteriaDTO.getFinalRegDate())){
            throw new IllegalArgumentException(MessageUtils.INVALID_INITIAL_REG_DATE.getDescription());
        }
    }

    private boolean isPeriodInvalid(LocalDateTime date1, LocalDateTime date2){
        return (date1 != null && date2 == null) || (date1 == null && date2 != null);
    }

    private void validateMovDates() {
        if(criteriaDTO.getFinalMovDate()==null && criteriaDTO.getInitialMovDate()==null)
            return;
        if(isPeriodInvalid(criteriaDTO.getFinalMovDate(),criteriaDTO.getInitialMovDate()))
            throw new IllegalArgumentException(MessageUtils.INVALID_MOVE_PERIOD.getDescription());
        if (criteriaDTO.getInitialMovDate().isAfter(criteriaDTO.getFinalMovDate())){
            throw new IllegalArgumentException(MessageUtils.INVALID_INITIAL_MOV_DATE.getDescription());
        }
    }
}
