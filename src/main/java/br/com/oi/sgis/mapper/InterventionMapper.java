package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.InterventionDTO;
import br.com.oi.sgis.entity.Intervention;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InterventionMapper {

    InterventionMapper INSTANCE = Mappers.getMapper(InterventionMapper.class);
    InterventionDTO toDTO(Intervention intervention);
    Intervention toModel(InterventionDTO interventionDTO);
}
