package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.entity.Situation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SituationMapper {

    SituationMapper INSTANCE = Mappers.getMapper(SituationMapper.class);

    Situation toModel(SituationDTO situationDTO);

    SituationDTO toDTO(Situation situation);
}
