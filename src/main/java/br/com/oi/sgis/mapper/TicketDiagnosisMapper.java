package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.TicketDiagnosisDTO;
import br.com.oi.sgis.entity.TicketDiagnosis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {DiagnosisMapper.class, TicketInterventionMapper.class, RepairTicketMapper.class})
public interface TicketDiagnosisMapper {

    TicketDiagnosisMapper INSTANCE = Mappers.getMapper(TicketDiagnosisMapper.class);

    @Mapping(source = "sequence", target = "id.ticketIntervention.id.sequence")
    @Mapping(source = "brNumber", target = "id.ticketIntervention.id.repairTicket.id")
    @Mapping(source = "diagnosis", target = "id.diagnosis.description")
    @Mapping(source = "diagnosisId", target = "id.diagnosis.id")
    TicketDiagnosis toModel(TicketDiagnosisDTO ticketDiagnosisDTO);

    @Mapping(target = "sequence", source = "id.ticketIntervention.id.sequence")
    @Mapping(target = "brNumber", source = "id.ticketIntervention.id.repairTicket.id")
    @Mapping(target = "diagnosis", source = "id.diagnosis.description")
    @Mapping(target = "diagnosisId", source = "id.diagnosis.id")
    TicketDiagnosisDTO toDTO(TicketDiagnosis ticketDiagnosis);


}
