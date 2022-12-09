package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.DepartmentUnityDTO;
import br.com.oi.sgis.entity.DepartmentUnity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {DepartmentMapper.class, AreaEquipamentMapper.class, StationMapper.class})
public interface DepartmentUnityMapper {

    DepartmentUnityMapper INSTANCE = Mappers.getMapper(DepartmentUnityMapper.class);
    @Mapping(target = "department", source = "id.department")
    @Mapping(target = "modelUnity", source = "id.equipament")
    DepartmentUnityDTO toDTO(DepartmentUnity departmentUnity);

    @Mapping(source = "dto.department", target = "id.department")
    @Mapping(source = "dto.modelUnity", target = "id.equipament")
    DepartmentUnity toModel(DepartmentUnityDTO dto);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("department.id", "id.department.id");
        mappedValues.put("modelUnity.id", "id.equipament.id");
        return mappedValues;
    }
}
