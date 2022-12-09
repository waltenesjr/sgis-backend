package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ComponentDTO;
import br.com.oi.sgis.entity.Component;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ComponentTypeMapper.class})
public interface ComponentMapper {
    ComponentMapper INSTANCE = Mappers.getMapper(ComponentMapper.class);

    Component toModel(ComponentDTO dto);
    ComponentDTO toDTO(Component component);

}
