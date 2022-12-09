package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ComponentMovTypeDTO;
import br.com.oi.sgis.entity.ComponentMovType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ComponentMovTypeMapper {

    ComponentMovTypeMapper INSTANCE = Mappers.getMapper(ComponentMovTypeMapper.class);

    @Mapping(source = "dto.type.id", target = "type")
    @Mapping(source = "dto.signal.id", target = "signal")
    ComponentMovType toModel(ComponentMovTypeDTO dto);

    @Mapping(source = "type", target = "type")
    @Mapping(source = "signal", target = "signal")
    ComponentMovTypeDTO toDTO(ComponentMovType componentMovType);
}
