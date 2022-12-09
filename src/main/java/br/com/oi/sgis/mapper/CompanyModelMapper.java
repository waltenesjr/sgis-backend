package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.CompanyModelDTO;
import br.com.oi.sgis.entity.CompanyModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {CompanyMapper.class, AreaEquipamentMapper.class, DepartmentMapper.class})
public interface CompanyModelMapper {

    CompanyModelMapper INSTANCE = Mappers.getMapper(CompanyModelMapper.class);

    @Mapping(source = "companyModelDTO.department", target = "id.department")
    @Mapping(source = "companyModelDTO.equipament", target = "id.equipament")
    @Mapping(source = "companyModelDTO.company", target = "id.company")
    CompanyModel toModel(CompanyModelDTO companyModelDTO);

    @Mapping(target = "department", source = "id.department")
    @Mapping(target = "equipament", source = "id.equipament")
    @Mapping(target = "company", source = "id.company")
    CompanyModelDTO toDTO(CompanyModel companyModel);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("department.id", "id.department.id");
        mappedValues.put("equipament.id", "id.equipament.id");
        mappedValues.put("company.id", "id.company.id");
        return mappedValues;
    }

}
