package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ItemEstimateDTO;
import br.com.oi.sgis.entity.ItemEstimate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = TicketInterventionMapper.class)
public interface ItemEstimateMapper {
    ItemEstimateMapper INSTANCE = Mappers.getMapper(ItemEstimateMapper.class);
    @Mapping(target = "ticketIntervention", source = "id.ticketIntervention")
    @Mapping(target = "estimate", source = "id.estimate.id")
    ItemEstimateDTO toDTO(ItemEstimate itemEstimate);

    @Mapping(source = "ticketIntervention", target = "id.ticketIntervention")
    @Mapping(source = "estimate", target = "id.estimate.id")
    ItemEstimate toModel(ItemEstimateDTO dto);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("ticketIntervention", "id.ticketIntervention");
        mappedValues.put("estimate", "id.estimate.id");
        return mappedValues;
    }
}
