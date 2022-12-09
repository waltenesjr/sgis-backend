package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.GenericQueryItemDTO;
import br.com.oi.sgis.entity.GenericQueryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {GenericQueryMapper.class})
public interface GenericQueryItemMapper {

    GenericQueryItemMapper INSTANCE = Mappers.getMapper(GenericQueryItemMapper.class);

    @Mapping(source = "genericQueryId", target = "id.genericQuery.id")
    @Mapping(source = "columnSequence", target = "id.columnSequence")
    GenericQueryItem toModel (GenericQueryItemDTO dto);

    @Mapping(source = "id.genericQuery.id", target = "genericQueryId")
    @Mapping(source = "id.columnSequence", target = "columnSequence")
    GenericQueryItemDTO toDTO (GenericQueryItem genericQueryItem);


    static Map<String, String> getMappedValues() {
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("genericQueryId", "id.genericQuery.id");
        return mappedValues;
    }
}
