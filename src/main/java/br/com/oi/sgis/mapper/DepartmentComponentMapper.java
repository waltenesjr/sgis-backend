package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.DepartmentComponentDTO;
import br.com.oi.sgis.entity.DepartmentComponent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {DepartmentMapper.class, ComponentMapper.class})
public interface DepartmentComponentMapper {

    DepartmentComponentMapper INSTANCE = Mappers.getMapper(DepartmentComponentMapper.class);

    @Mapping(target = "department", source = "id.department")
    @Mapping(target = "component", source = "id.component")
    DepartmentComponentDTO toDTO(DepartmentComponent departmentComponent);

    @Mapping(source = "dto.department", target = "id.department")
    @Mapping(source = "dto.component", target = "id.component")
    DepartmentComponent toModel(DepartmentComponentDTO dto);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("department.id", "id.department.id");
        mappedValues.put("component.id", "id.component.id");
        return mappedValues;
    }
}
