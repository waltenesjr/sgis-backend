package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.TechniqueDTO;
import br.com.oi.sgis.entity.Technique;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TechniqueMapper {

    TechniqueMapper INSTANCE = Mappers.getMapper(TechniqueMapper.class) ;

    TechniqueDTO toDTO(Technique technique);

    Technique toModel(TechniqueDTO dto);
}
