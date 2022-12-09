package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ParameterDTO;
import br.com.oi.sgis.entity.Parameter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CompanyMapper.class})
public interface ParameterMapper {

    ParameterMapper INSTANCE = Mappers.getMapper(ParameterMapper.class);

    ParameterDTO toDTO(Parameter parameter);
    Parameter toModel(ParameterDTO dto);
}
