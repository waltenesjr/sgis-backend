package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.CableMovementDTO;
import br.com.oi.sgis.entity.CableMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {UnityMapper.class, TechnicalStaffMapper.class, DepartmentMapper.class, ElectricalPropMapper.class, ComponentMovTypeMapper.class})
public interface CableMovementMapper {

    CableMovementMapper INSTANCE = Mappers.getMapper(CableMovementMapper.class);
    @Mapping(source = "dto.sequence", target = "id.sequence")
    @Mapping(source = "dto.unity", target = "id.unity")
    CableMovement toModel(CableMovementDTO dto);

    @Mapping(source = "id.sequence", target = "sequence")
    @Mapping(source = "id.unity", target = "unity")
    CableMovementDTO toDTO(CableMovement cableMovement);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("sequence", "id.sequence.id");
        mappedValues.put("unity.id", "id.unity.id");
        return mappedValues;
    }
}
