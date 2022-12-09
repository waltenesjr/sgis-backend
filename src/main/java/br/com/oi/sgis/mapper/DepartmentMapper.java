package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {AddressMapper.class})
public interface DepartmentMapper {

    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    @Mapping(source = "departmentDTO.managerName", target = "manager.technicianName")
    @Mapping(source = "departmentDTO.contactName", target = "contact.technicianName")
    @Mapping(source = "departmentDTO.managerRegisterNum", target = "manager.id")
    @Mapping(source = "departmentDTO.contactRegisterNum", target = "contact.id")
    Department toModel(DepartmentDTO departmentDTO);

    @Mapping(target = "managerName", source = "manager.technicianName")
    @Mapping(target = "contactName", source = "contact.technicianName")
    @Mapping(target = "managerRegisterNum", source = "manager.id")
    @Mapping(target = "contactRegisterNum", source = "contact.id")
    DepartmentDTO toDTO(Department department);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("managerName", "manager.technicianName");
        mappedValues.put("contactName", "contact.technicianName");
        mappedValues.put("managerRegisterNum", "manager.id");
        mappedValues.put("contactRegisterNum", "contact.id");
        return mappedValues;
    }
}
