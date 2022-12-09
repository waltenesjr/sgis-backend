package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.EstimateDTO;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstimateValidator implements Validator<EstimateDTO> {
    @Override
    public void validate(EstimateDTO estimateDTO) {
        validateRepairCenter(estimateDTO.getDepartment());
        validateDate(estimateDTO);
    }

    private void validateDate(EstimateDTO estimateDTO) {
        Utils.isPeriodInvalid(estimateDTO.getDate(), estimateDTO.getExpirationDate());
    }

    private void validateRepairCenter(DepartmentDTO department) {
        String departmentUser = Utils.getUser().getDepartmentCode().getId();
        if(!department.getId().equals(departmentUser)){
            throw new IllegalArgumentException(MessageUtils.ESTIMATE_REPAIR_CENTER_ERROR.getDescription());
        }
    }
}
