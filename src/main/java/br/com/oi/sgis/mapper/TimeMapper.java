package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.TimeDTO;
import br.com.oi.sgis.entity.Time;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {InterventionMapper.class, AreaEquipamentMapper.class})
public interface TimeMapper {

    TimeMapper INSTANCE = Mappers.getMapper(TimeMapper.class);

    @Mapping(source = "dto.unityModel", target = "id.unityModel")
    @Mapping(source = "dto.intervention", target = "id.intervention")
    Time toModel(TimeDTO dto);

    @Mapping(target = "unityModel", source = "time.id.unityModel")
    @Mapping(target = "intervention", source = "time.id.intervention")
    TimeDTO toDTO(Time time);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("unityModel.id", "id.unityModel.id");
        mappedValues.put("intervention.id", "id.intervention.id");
        return mappedValues;
    }
}
