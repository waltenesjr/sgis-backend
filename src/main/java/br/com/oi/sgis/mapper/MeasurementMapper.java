package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.MeasurementDTO;
import br.com.oi.sgis.entity.Measurement;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeasurementMapper {

    MeasurementMapper INSTANCE = Mappers.getMapper(MeasurementMapper.class);
    MeasurementDTO toDTO(Measurement measurement);
    Measurement toModel(MeasurementDTO measurementDTO);
}
