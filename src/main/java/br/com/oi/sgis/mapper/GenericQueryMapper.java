package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.GenericQueryDTO;
import br.com.oi.sgis.entity.GenericQuery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {GenericQueryTypeMapper.class, TechnicalStaffMapper.class, GenericQueryItemMapper.class})
public interface GenericQueryMapper {

    GenericQueryMapper INSTANCE = Mappers.getMapper(GenericQueryMapper.class);

    @Mapping(source = "genericQueryTypeId", target = "genericQueryType.id")
    @Mapping(source = "technicalStaffId", target = "technicalStaff.id")
    @Mapping(source = "columns", target = "genericQueryItems")
    GenericQuery toModel (GenericQueryDTO dto);

    @Mapping(source = "genericQueryType.id", target = "genericQueryTypeId")
    @Mapping(source = "technicalStaff.id", target = "technicalStaffId")
    @Mapping(source = "genericQueryItems", target = "columns")
    GenericQueryDTO toDTO (GenericQuery genericQuery);


    static Map<String, String> getMappedValues() {
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("genericQueryTypeId", "genericQueryType.id");
        mappedValues.put("technicalStaffId", "technicalStaff.id");
        mappedValues.put("columns", "genericQueryItems");
        return mappedValues;
    }
}
