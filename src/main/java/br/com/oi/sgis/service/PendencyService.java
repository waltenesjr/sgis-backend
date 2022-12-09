package br.com.oi.sgis.service;

import br.com.oi.sgis.entity.Pendency;
import br.com.oi.sgis.entity.RepairTicket;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.repository.PendencyRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PendencyService {

    private final PendencyRepository pendencyRepository;

    public void createPendencyFromRepairTicket(RepairTicket repairTicket) {

        Pendency pendency = Pendency.builder()
                .id(repairTicket.getUnity().getUnityCode().getId())
                .equipament(repairTicket.getUnity().getUnityCode())
                .unity(repairTicket.getUnity())
                .equipament(repairTicket.getUnity().getUnityCode())
                .originMov(SituationEnum.REP.getCod())
                .departmentDestination(repairTicket.getDevolutionDepartment())
                .departmentOrigin(repairTicket.getOriginDepartment())
                .technician(repairTicket.getTechnician())
                .operator(repairTicket.getOperator())
                .observation("BR: " + repairTicket.getId())
                .initialDate(LocalDateTime.now())
                .build();

        try {
            pendencyRepository.insertNewPendency(pendency.getEquipament().getId(), repairTicket.getUnity().getId(), pendency.getOriginMov(), pendency.getDepartmentOrigin().getId(),
                    pendency.getDepartmentDestination().getId(),pendency.getTechnician().getId(), pendency.getOperator().getId(),
                    pendency.getObservation(),pendency.getInitialDate() );
        }catch (Exception e){
            throw new IllegalArgumentException("Erro durante o cadastro da pendência");
        }
    }
}
