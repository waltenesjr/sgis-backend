package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.entity.Level;
import br.com.oi.sgis.entity.RepairTicket;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.exception.RepairTicketException;
import br.com.oi.sgis.service.DepartmentService;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepairTicketValidator implements Validator<RepairTicket> {

    private final DepartmentService departmentService;

    @Override @SneakyThrows
    public void validate(RepairTicket repairTicket) {
        validateRepairCenter(repairTicket);
        validateNormalizationDate(repairTicket);
        validateCpsCsDeliverDateAfterNow(repairTicket);
        validateCpsCsDeliverDate(repairTicket);
        validateUserPermission(repairTicket.getUnity());

    }

    private void validateNormalizationDate(RepairTicket repairTicket) throws RepairTicketException {
        if (repairTicket.getNormalizationDate().toLocalDate().isAfter(LocalDate.now())){
            throw new RepairTicketException(MessageUtils.REPAIR_TICKET_INVALID_NORMALIZATION_DT.getDescription());
        }
    }

    private void validateCpsCsDeliverDateAfterNow(RepairTicket repairTicket) throws RepairTicketException {
        if (repairTicket.getCspCsDeliverDate().toLocalDate().isAfter(LocalDate.now())){
            throw new RepairTicketException(MessageUtils.REPAIR_TICKET_DELIVER_DT_AFTER_NOW.getDescription());
        }
    }

    private void validateCpsCsDeliverDate(RepairTicket repairTicket) throws RepairTicketException {
        if (repairTicket.getCspCsDeliverDate().isBefore(repairTicket.getNormalizationDate())){
            throw new RepairTicketException(MessageUtils.REPAIR_TICKET_INVALID_DELIVER_DT.getDescription());
        }
    }

    private void validateRepairCenter(RepairTicket repairTicket) throws DepartmentNotFoundException, RepairTicketException {
        DepartmentDTO repairCenter = departmentService.findById(repairTicket.getRepairCenterDepartment().getId());
        if(!repairCenter.isRepairCenter())
            throw new RepairTicketException(MessageUtils.REPAIR_CENTER_NOT_ABLE_ERROR.getDescription());
    }
    private void validateUserPermission(Unity unity) throws RepairTicketException {
        List<Integer> userLevels = Utils.getUser().getLevels().stream().map(Level::getLvl).collect(Collectors.toList());
        String userDepartment = Utils.getUser().getDepartmentCode().getId();
        if(!userLevels.contains(0) && !unity.getResponsible().getId().equals(userDepartment)){
            throw new RepairTicketException(String.format(MessageUtils.REPAIR_TICKET_PERMISION_ERROR.getDescription(),Utils.getUser().getId()));
        }
    }



}
