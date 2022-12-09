package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ManufacturerDTO;
import br.com.oi.sgis.entity.Manufacturer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ManufacturerMapper {

    ManufacturerMapper INSTANCE = Mappers.getMapper(ManufacturerMapper.class);

    Manufacturer toModel(ManufacturerDTO manufacturerDTO);

    ManufacturerDTO toDTO(Manufacturer manufacturer);
}
