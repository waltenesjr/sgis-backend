package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.BoxTypeDTO;
import br.com.oi.sgis.entity.BoxType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoxTypeMapper {

    BoxTypeMapper INSTANCE = Mappers.getMapper(BoxTypeMapper.class);

    BoxType toModel(BoxTypeDTO dto);
    BoxTypeDTO toDTO(BoxType boxType);
}
