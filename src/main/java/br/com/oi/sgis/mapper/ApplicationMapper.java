package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ApplicationDTO;
import br.com.oi.sgis.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationMapper {
    ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

    Application toModel(ApplicationDTO applicationDTO);

    ApplicationDTO toDTO(Application application);
}
