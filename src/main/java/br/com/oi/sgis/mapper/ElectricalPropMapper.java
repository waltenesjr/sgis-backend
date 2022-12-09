package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ElectricalPropDTO;
import br.com.oi.sgis.entity.ElectricalProperty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ElectricalPropMapper {

    ElectricalPropMapper INSTANCE = Mappers.getMapper(ElectricalPropMapper.class);

    @Mapping(source = "measurement", target = "measurement.id")
    ElectricalProperty toModel(ElectricalPropDTO electricalPropDTO);

    @Mapping(source = "measurement.id", target = "measurement")
    ElectricalPropDTO toDTO(ElectricalProperty electricalProperty);
}
