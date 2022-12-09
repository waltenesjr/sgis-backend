package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.TicketInterventionDTO;
import br.com.oi.sgis.entity.TicketIntervention;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {InterventionMapper.class, RepairTicketMapper.class, TechnicalStaffMapper.class, UnityMapper.class, TicketDiagnosisMapper.class, ComponentMovMapper.class})
public interface TicketInterventionMapper {

    TicketInterventionMapper INSTANCE = Mappers.getMapper(TicketInterventionMapper.class);

    @Mapping(source = "id.sequence", target = "sequence")
    @Mapping(source = "id.repairTicket", target = "repairTicket")
    @Mapping(target = "itemEstimate", ignore = true)
    TicketInterventionDTO toDTO(TicketIntervention ticketIntervention);


    @Mapping(target = "id.sequence", source = "sequence")
    @Mapping(target = "id.repairTicket", source = "repairTicket")
    @Mapping(target = "itemEstimate", ignore = true)
    TicketIntervention toModel(TicketInterventionDTO ticketInterventionDTO);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("sequence", "id.sequence");
        mappedValues.put("repairTicket.id", "id.repairTicket.id");
        return mappedValues;
    }


}
