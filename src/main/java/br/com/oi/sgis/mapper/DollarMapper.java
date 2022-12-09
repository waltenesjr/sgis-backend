package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.DollarDTO;
import br.com.oi.sgis.entity.Dollar;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DollarMapper {
    DollarMapper INSTANCE = Mappers.getMapper(DollarMapper.class);

    Dollar toModel(DollarDTO dto);
    DollarDTO toDTO(Dollar dollar);
}
