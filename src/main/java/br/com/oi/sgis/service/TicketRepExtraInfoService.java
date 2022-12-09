package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ComponentMovDTO;
import br.com.oi.sgis.dto.TicketIntervExtraInfoDTO;
import br.com.oi.sgis.dto.TicketRepExtraInfoDTO;
import br.com.oi.sgis.entity.RepairTicket;
import br.com.oi.sgis.entity.TicketDiagnosis;
import br.com.oi.sgis.mapper.ComponentMovMapper;
import br.com.oi.sgis.mapper.TicketDiagnosisMapper;
import br.com.oi.sgis.repository.ComponentMovRepository;
import br.com.oi.sgis.repository.TicketDiagnosisRepository;
import br.com.oi.sgis.repository.TicketInterventionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TicketRepExtraInfoService {

    private TicketInterventionRepository ticketInterventionRepository;
    private TicketDiagnosisRepository ticketDiagnosisRepository;
    private static final TicketDiagnosisMapper ticketDiagnosisMapper = TicketDiagnosisMapper.INSTANCE;
    private ComponentMovRepository componentMovRepository;
    private static final ComponentMovMapper componentMovMapper = ComponentMovMapper.INSTANCE;

    public TicketRepExtraInfoDTO getExtraInfo(RepairTicket repairTicket){
        TicketRepExtraInfoDTO extraInfoDTO = TicketRepExtraInfoDTO.builder()
                .openDate(repairTicket.getOpenDate())
                .acceptDate(repairTicket.getAcceptDate())
                .devolutionDate(repairTicket.getDevolutionDate())
                .cancelDate(repairTicket.getCancelDate())
                .value(repairTicket.getRepairValue())
                .build();

        List<Long> sequences = getSequences(repairTicket, extraInfoDTO);

        setDiagnosis(extraInfoDTO, sequences);
        setComponents(extraInfoDTO, sequences);

        return extraInfoDTO;
    }

    private void setComponents(TicketRepExtraInfoDTO extraInfoDTO, List<Long> sequences) {
        List<ComponentMovDTO> componentMovDTOS = componentMovRepository.findAllByTicketIntervention(sequences).stream().map(componentMovMapper::toDTO).collect(Collectors.toList());
        extraInfoDTO.setComponentMov(componentMovDTOS);
    }

    private void setDiagnosis(TicketRepExtraInfoDTO extraInfoDTO, List<Long> sequences) {
        List<TicketDiagnosis> listDiagnosis = ticketDiagnosisRepository.findAllByTicketIntervention(sequences);
        extraInfoDTO.setDiagnoses(listDiagnosis.stream().map(ticketDiagnosisMapper::toDTO).collect(Collectors.toList()));
    }

    private List<Long> getSequences(RepairTicket repairTicket, TicketRepExtraInfoDTO extraInfoDTO) {
        List<TicketIntervExtraInfoDTO> ticketInterventionDTO =  ticketInterventionRepository.findByRepairTicket(repairTicket.getId());
        List<Long> sequences = ticketInterventionDTO.parallelStream().map(x -> x.getTicketInterventionID().getSequence()).collect(Collectors.toList());
        extraInfoDTO.setTicketIntervention(ticketInterventionDTO);
        return sequences;
    }

}
