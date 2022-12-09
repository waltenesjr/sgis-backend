package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ModelDTO;
import br.com.oi.sgis.entity.Model;
import br.com.oi.sgis.entity.ModelId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = ModelId.class)
public interface ModelMapper {

    ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);

    @Mapping(source = "modelCod", target = "id.modelCod")
    @Mapping(source = "manufacturerCod", target = "id.manufacturerCod.id")
    @Mapping(source = "manufacturerDescription", target = "id.manufacturerCod.description")
    Model toModel(ModelDTO modelDTO);

    @Mapping(target = "modelCod", source = "id.modelCod")
    @Mapping(target = "manufacturerCod", source = "id.manufacturerCod.id")
    @Mapping(target = "manufacturerDescription", source = "id.manufacturerCod.description")
    ModelDTO toDTO(Model model);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("modelCod", "id.modelCod");
        mappedValues.put("manufacturerCod", "id.manufacturerCod.id");
        mappedValues.put("manufacturerDescription", "id.manufacturerCod.description");
        return mappedValues;
    }
}
