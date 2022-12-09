package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.UnityHistoricalViewDTO;
import br.com.oi.sgis.entity.view.UnityHistoricalView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UnityHistoricalViewMapper {

    UnityHistoricalViewMapper INSTANCE = Mappers.getMapper(UnityHistoricalViewMapper.class);

    UnityHistoricalViewDTO toDTO(UnityHistoricalView model);

    UnityHistoricalView toModel(UnityHistoricalViewDTO dto);
}
