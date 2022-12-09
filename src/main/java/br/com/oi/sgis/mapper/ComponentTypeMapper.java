package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ComponentTypeDTO;
import br.com.oi.sgis.entity.ComponentType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ComponentTypeMapper {
    ComponentTypeMapper INSTANCE = Mappers.getMapper(ComponentTypeMapper.class);

    ComponentType toModel(ComponentTypeDTO dto);
    ComponentTypeDTO toDTO(ComponentType componentType);
}
