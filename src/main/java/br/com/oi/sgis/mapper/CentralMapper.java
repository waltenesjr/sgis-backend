package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.CentralDTO;
import br.com.oi.sgis.entity.Central;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {StationMapper.class})
public interface CentralMapper {

    CentralMapper INSTANCE = Mappers.getMapper(CentralMapper.class);

    CentralDTO toDTO(Central central);

    Central toModel(CentralDTO centralDTO);
}
