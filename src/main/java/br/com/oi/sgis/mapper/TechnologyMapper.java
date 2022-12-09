package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.TechnologyDTO;
import br.com.oi.sgis.entity.Technology;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TechnologyMapper {

    TechnologyMapper INSTANCE = Mappers.getMapper(TechnologyMapper.class);

    Technology toModel(TechnologyDTO dto);
    TechnologyDTO toDTO(Technology technology);
}
