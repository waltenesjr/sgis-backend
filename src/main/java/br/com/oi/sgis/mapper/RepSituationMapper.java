package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.entity.RepSituation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RepSituationMapper {

    RepSituationMapper INSTANCE = Mappers.getMapper(RepSituationMapper.class);

    RepSituation toModel(SituationDTO situationDTO);

    SituationDTO toDTO(RepSituation repSituation);

}
