package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ElectricalPropUnityDTO;
import br.com.oi.sgis.entity.ElectricalPropUnity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ElectricalPropUnityMapper {

    ElectricalPropUnityMapper INSTANCE = Mappers.getMapper(ElectricalPropUnityMapper.class);

    @Mapping(source = "property", target = "id.properties.id")
    @Mapping(source = "unity", target = "id.unity.id")
    ElectricalPropUnity toModel(ElectricalPropUnityDTO electricalPropUnityDTO);

    @Mapping(source = "id.properties.id", target = "property")
    @Mapping(source = "id.unity.id", target = "unity")
    ElectricalPropUnityDTO toDTO(ElectricalPropUnity electricalPropUnity);
}
