package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.BoxDTO;
import br.com.oi.sgis.entity.Box;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper
public interface BoxMapper {

    BoxMapper INSTANCE = Mappers.getMapper(BoxMapper.class);

    @Mapping(source = "station", target = "station.id")
    @Mapping(source = "boxType", target = "boxType.id")
    Box toModel(BoxDTO dto);

    @Mapping(target = "station", source = "station.id")
    @Mapping(target = "boxType", source = "boxType.id")
    BoxDTO toDTO(Box box);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("station", "station.id");
        mappedValues.put("boxType", "boxType.id");
        return mappedValues;
    }
}
