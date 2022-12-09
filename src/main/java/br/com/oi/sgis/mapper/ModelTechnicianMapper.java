package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ModelTechnicianDTO;
import br.com.oi.sgis.entity.ModelTechnician;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {DepartmentMapper.class, AreaEquipamentMapper.class, TechnicalStaffMapper.class})
public interface ModelTechnicianMapper {

    ModelTechnicianMapper INSTANCE = Mappers.getMapper(ModelTechnicianMapper.class);
    @Mapping(source = "id.department", target = "department")
    @Mapping(source = "id.model", target = "model")
    @Mapping(source = "id.technicalStaff", target = "technicalStaff")
    ModelTechnicianDTO toDTO(ModelTechnician modelTechnician);

    @Mapping(source = "department", target = "id.department")
    @Mapping(source = "model", target = "id.model")
    @Mapping(source = "technicalStaff", target = "id.technicalStaff")
    ModelTechnician toModel(ModelTechnicianDTO dto);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("department.id", "id.department.id");
        mappedValues.put("model.id", "id.model.id");
        mappedValues.put("technicalStaff.id", "id.technicalStaff.id");
        return mappedValues;
    }
}
