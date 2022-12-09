package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ComponentMovDTO;
import br.com.oi.sgis.entity.ComponentMov;
import br.com.oi.sgis.entity.ComponentMovType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ComponentMovTypeMapper.class, ComponentMovType.class, TechnicalStaffMapper.class, TicketInterventionMapper.class})
public interface ComponentMovMapper {

    ComponentMovMapper INSTANCE = Mappers.getMapper(ComponentMovMapper.class);

    @Mapping(source = "component", target = "id.departmentComponent.id.component.id")
    @Mapping(source = "componentDescription", target = "id.departmentComponent.id.component.description")
    @Mapping(source = "departmentQuantity", target = "id.departmentComponent.quantity")
    @Mapping(source = "sequence", target = "id.sequence")
    @Mapping(source = "departmentId", target = "id.departmentComponent.id.department.id")
    @Mapping(source = "brNumber", target = "ticketIntervention.id.repairTicket.id")
    @Mapping(source = "sequenceIntervention", target = "ticketIntervention.id.sequence")
    ComponentMov toModel(ComponentMovDTO componentMovDTO);

    @Mapping(target = "component", source = "id.departmentComponent.id.component.id")
    @Mapping(target = "componentDescription", source = "id.departmentComponent.id.component.description")
    @Mapping(target = "departmentQuantity", source = "id.departmentComponent.quantity")
    @Mapping(target = "sequence", source = "id.sequence")
    @Mapping(target = "departmentId", source = "id.departmentComponent.id.department.id")
    @Mapping(target = "brNumber", source = "ticketIntervention.id.repairTicket.id")
    @Mapping(target = "sequenceIntervention", source = "ticketIntervention.id.sequence")
    ComponentMovDTO toDTO(ComponentMov componentMov);

}
