package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.TicketInterventionDTO;
import br.com.oi.sgis.dto.UnityDTO;
import br.com.oi.sgis.entity.*;
import br.com.oi.sgis.exception.UnityNotFoundException;
import br.com.oi.sgis.repository.RepairTicketRepository;
import br.com.oi.sgis.repository.TechnicalStaffRepository;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketInterventionValidator  implements Validator<TicketInterventionDTO> {

    private final RepairTicketRepository repairTicketRepository;
    private final TechnicalStaffRepository technicalStaffRepository;
    private final UnityRepository unityRepository;

    @Override
    public void validate(TicketInterventionDTO ticketInterventionDTO) {
        RepairTicket repairTicket = repairTicketRepository.findById(ticketInterventionDTO.getRepairTicket().getBrNumber())
                .orElseThrow(()->new IllegalArgumentException(MessageUtils.REPAIR_TICKET_NOT_FOUND_BY_ID.getDescription() + ticketInterventionDTO.getRepairTicket().getBrNumber()));
        validateUnitySituation(ticketInterventionDTO.getUnity());
        validateRepairCenter(repairTicket.getRepairCenterDepartment());
        if(Boolean.TRUE.equals(ticketInterventionDTO.getExternalRepair())) {
            validateExternalRepair(ticketInterventionDTO, repairTicket);
        }
        else {
            validateInternalRepair(ticketInterventionDTO, repairTicket);
        }

    }

    private void validateExternalRepair(TicketInterventionDTO ticketInterventionDTO, RepairTicket repairTicket) {
        validateSituationExternal(repairTicket.getSituation());
        validateInterventionExternal(ticketInterventionDTO, repairTicket);
    }

    private void validateInterventionExternal(TicketInterventionDTO ticketInterventionDTO, RepairTicket repairTicket) {
        TechnicalStaff user = technicalStaffRepository.getById(Utils.getUser().getId());
        if(ticketInterventionDTO.getIntervention().getId().equals("3") && user.getDepartmentCode().isObligateFowarding())
                throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_EXT_SAVE_TRIAGE_ERROR.getDescription());

        List<String> situations = List.of("EOR","EGO", "EGA");
        if(situations.contains(repairTicket.getSituation().getId()) &&  user.getDepartmentCode().isNotDesignatedBloq())
            validateMantainer(repairTicket.getMaintainer(), ticketInterventionDTO);

    }

    private void validateMantainer(Company maintainer, TicketInterventionDTO ticketInterventionDTO) {
        if(maintainer == null)
            throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_EXT_MANTAINER_NULL_ERROR.getDescription());

        if(!maintainer.getId().equals(ticketInterventionDTO.getCompanyEstimate()))
            throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_EXT_MANTAINER_ERROR.getDescription());

    }

    private void validateSituationExternal(RepSituation situation) {
        List<String> situations = List.of("EOR","ECT","EGO","EGC", "EGA");
        if(!situations.contains(situation.getId())){
            throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_SITUATION_EXT_ERROR.getDescription());
        }
    }

    private void validateInternalRepair(TicketInterventionDTO ticketInterventionDTO, RepairTicket repairTicket) {
        validateSituation(repairTicket.getSituation());
        validateIntervention(ticketInterventionDTO, repairTicket);
    }

    private void validateIntervention(TicketInterventionDTO ticketInterventionDTO, RepairTicket repairTicket) {
        TechnicalStaff user = technicalStaffRepository.getById(Utils.getUser().getId());
        if(ticketInterventionDTO.getIntervention().getId().equals("3")){
            if(user.getDepartmentCode().isUnscreenedBlock())
                throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_SAVE_TRIAGE_ERROR.getDescription());
            if(!repairTicket.getSituation().getId().equals("ERI")&&user.getDepartmentCode().isObligateFowarding())
                throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_SAVE_FORWARDING_ERROR.getDescription());
        }
        if(repairTicket.getRepairTechnician()==null && user.getDepartmentCode().isNotDesignatedBloq())
            throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_SAVE_TECHNICIAN_ERROR.getDescription());

    }

    @SneakyThrows
    private void validateUnitySituation(UnityDTO unityDTO)  {
        Unity unity = unityRepository.findById(unityDTO.getId()).orElseThrow(()-> new UnityNotFoundException(MessageUtils.UNITY_NOT_FOUND_BY_ID.getDescription() + unityDTO.getId()));
        if(!unity.getSituationCode().getId().equals("REP")){
            throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_UNITY_SIT_ERROR.getDescription());
        }
    }

    private void validateSituation(RepSituation situation) {
        if(situation.getId().equals("ABE")){
            throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_SITUATION_ERROR.getDescription());
        }
    }

    private void validateRepairCenter(Department repairCenterDepartment) {
        String userDepartment = Utils.getUser().getDepartmentCode().getId();
        if(!repairCenterDepartment.getId().equals(userDepartment))
            throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_CR_ERROR.getDescription());
    }
}
