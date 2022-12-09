package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.EquipamentTypeDTO;
import br.com.oi.sgis.entity.EquipamentType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EquipamentTypeMapper {

    EquipamentTypeMapper INSTANCE = Mappers.getMapper(EquipamentTypeMapper.class);

    EquipamentType toModel(EquipamentTypeDTO equipamentTypeDTO);

    EquipamentTypeDTO toDTO(EquipamentType equipamentType);
}
