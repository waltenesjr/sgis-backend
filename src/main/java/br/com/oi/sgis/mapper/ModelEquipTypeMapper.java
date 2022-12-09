package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ModelEquipTypeDTO;
import br.com.oi.sgis.entity.ModelEquipamentType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CompanyMapper.class, TechnologyMapper.class, TechnicalStaffMapper.class})
public interface ModelEquipTypeMapper {

    ModelEquipTypeMapper INSTANCE = Mappers.getMapper(ModelEquipTypeMapper.class);

    ModelEquipTypeDTO toDTO(ModelEquipamentType modelEquipamentType);
    ModelEquipamentType toModel(ModelEquipTypeDTO modelEquipTypeDTO);
}
