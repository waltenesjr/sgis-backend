package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.GenericQueryTypeDTO;
import br.com.oi.sgis.entity.GenericQueryType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = DomainMapper.class)
public interface GenericQueryTypeMapper {

    GenericQueryTypeMapper INSTANCE = Mappers.getMapper(GenericQueryTypeMapper.class);

    GenericQueryTypeDTO toDTO(GenericQueryType genericQueryType);

    GenericQueryType toModel(GenericQueryTypeDTO dto);
}
