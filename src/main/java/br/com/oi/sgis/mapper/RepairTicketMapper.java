package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.RepairTicketDTO;
import br.com.oi.sgis.entity.RepairTicket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {CentralMapper.class, UnityMapper.class, DepartmentMapper.class, TechnicalStaffMapper.class, AddressMapper.class, CompanyMapper.class, ContractMapper.class}, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RepairTicketMapper {

    RepairTicketMapper INSTANCE = Mappers.getMapper(RepairTicketMapper.class);

    @Mapping(target = "station", source = "station.id")
    @Mapping(target = "technician", source = "technician.id")
    @Mapping(target = "originDepartment", source = "originDepartment.id")
    @Mapping(target = "operator", source = "operator.id")
    @Mapping(target = "repairCenterDepartment", source = "repairCenterDepartment.id")
    @Mapping(target = "devolutionDepartment", source = "devolutionDepartment.id")
    @Mapping(target = "brNumber", source = "id")
    @Mapping(target = "priority", source = "priority")
    @Mapping(target = "unityId", source = "unity.id")
    RepairTicketDTO toDTO(RepairTicket repairTicket);

    @Mapping(target = "station.id", source = "station")
    @Mapping(target = "technician.id", source = "technician")
    @Mapping(target = "originDepartment.id", source = "originDepartment")
    @Mapping(target = "operator.id", source = "operator")
    @Mapping(target = "repairCenterDepartment.id", source = "repairCenterDepartment")
    @Mapping(target = "devolutionDepartment.id", source = "devolutionDepartment")
    @Mapping(target = "priority", source = "priority.cod")
    @Mapping(target = "id", source = "brNumber")
    @Mapping(target = "unity.id", source = "unityId")
    RepairTicket toModel(RepairTicketDTO dto);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("station", "station.id");
        mappedValues.put("technician", "technician.id");
        mappedValues.put("originDepartment", "originDepartment.id");
        mappedValues.put("operator", "operator.id");
        mappedValues.put("repairCenterDepartment", "repairCenterDepartment.id");
        mappedValues.put("devolutionDepartment", "devolutionDepartment.id");
        mappedValues.put("brNumber", "id");
        mappedValues.put("unityId", "unity.id");
        return mappedValues;
    }

}
