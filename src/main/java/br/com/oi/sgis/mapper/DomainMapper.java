package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.DomainDTO;
import br.com.oi.sgis.entity.Domain;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomainMapper {

    DomainMapper INSTANCE = Mappers.getMapper(DomainMapper.class);

    Domain toModel(DomainDTO dto);
    DomainDTO toDTO(Domain domain);
}
