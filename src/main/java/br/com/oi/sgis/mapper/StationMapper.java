package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.StationDTO;
import br.com.oi.sgis.entity.Station;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {AddressMapper.class})
public interface StationMapper {

    StationMapper INSTANCE = Mappers.getMapper(StationMapper.class);

    Station toModel(StationDTO stationDTO);

    StationDTO toDTO(Station station);
}
